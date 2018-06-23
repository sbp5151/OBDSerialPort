package com.jld.obdserialport;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.jld.obdserialport.runnable.BindDeviceRun;
import com.jld.obdserialport.runnable.LocationReceiveRun;
import com.jld.obdserialport.runnable.OBDReceiveRun;

import org.greenrobot.eventbus.EventBus;

/**
 * 1、串口连接，失败重连
 * 2、注册EventBus接收SerialPortIOManager数据
 */
public class SelfStartService extends Service {

    private static final String TAG = "SelfStartService";
    private MyBinder mMyBinder;
    public LocationReceiveRun mLocationReceive;
    private OBDReceiveRun mObdReceive;
    private BindDeviceRun mBindDeviceRun;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        //OBD数据获取线程
        mObdReceive = new OBDReceiveRun(this);
        //开启GPS信息获取线程
        mLocationReceive = new LocationReceiveRun(this);
        //激光绑定线程
        mBindDeviceRun = new BindDeviceRun(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }

    public class MyBinder extends Binder {
        public void sendData(String data) {
            mObdReceive.addWriteData(data);
        }

        public boolean isConnect() {
            return mObdReceive.isConnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy:");
        mObdReceive.disConnect();
        mLocationReceive.removeUpdates();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
