package com.jld.obdserialport;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.jld.obdserialport.runnable.BindDeviceRun;
import com.jld.obdserialport.runnable.LocationReceiveRun;
import com.jld.obdserialport.runnable.OBDReceiveRun;
import com.jld.obdserialport.utils.Constant;

import org.greenrobot.eventbus.EventBus;

/**
 * 1、启动三大runnable
 * 2、与TestActivity进行数据通信
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
        getTelephonyInfo();
        //极光绑定线程
        mBindDeviceRun = new BindDeviceRun(this);
        //OBD数据获取线程
        mObdReceive = new OBDReceiveRun(this);
        //开启GPS信息获取线程
        mLocationReceive = new LocationReceiveRun(this);
    }

    private void getTelephonyInfo() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "read permission phoneNum fail: ");
            return;
        }
        //获取手机号码
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();//获取智能设备唯一编号
        String te1 = tm.getLine1Number();//获取本机号码
        Constant.ICCID = tm.getSimSerialNumber();//获得SIM卡的序号
        String imsi = tm.getSubscriberId();//得到用户Id
        Log.d(TAG, "deviceid:" + deviceid + "\n\r" + "te1:" + te1 + "\n\r" + "ICCID:" + Constant.ICCID + "\n\r" + "imsi:" + imsi);
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
