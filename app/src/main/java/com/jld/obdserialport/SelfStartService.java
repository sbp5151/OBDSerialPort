package com.jld.obdserialport;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.jld.obdserialport.http.FileHttpUtil;
import com.jld.obdserialport.http.OtherHttpUtil;
import com.jld.obdserialport.runnable.BindDeviceRun;
import com.jld.obdserialport.runnable.LocationReceiveRun;
import com.jld.obdserialport.runnable.MediaRun;
import com.jld.obdserialport.runnable.OBDReceiveRun;
import com.jld.obdserialport.util.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

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
    private MediaRun mMediaRun;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        //极光绑定任务
        mBindDeviceRun = new BindDeviceRun(this);
        //OBD数据获取任务
        mObdReceive = new OBDReceiveRun(this);
        //开启GPS信息获取任务
        mLocationReceive = new LocationReceiveRun(this);
        //开启媒体任务
        mMediaRun = new MediaRun(this);
        OtherHttpUtil.build().checkApkUpdate(this, new OtherHttpUtil.ApkCheckUpdateListener() {
            @Override
            public void onApkDownload(String download) {
                String apkName = download.substring(download.lastIndexOf("/") + 1).replace(".1", "");
                final File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CarFuture" + File.separator + apkName);
                FileHttpUtil.build().fileDownload(download, saveFile.getAbsolutePath(), new FileHttpUtil.DownloadFileListener() {
                    @Override
                    public void onDownloadFailed() {
                        Log.d(TAG, "onDownloadFailed");
                    }

                    @Override
                    public void onDownloadSucceed() {
                        Log.d(TAG, "onDownloadSucceed:" + saveFile);
                        AppUtils.installApp(saveFile);
                    }

                    @Override
                    public void onDownloadLoading(long progress) {
                        Log.d(TAG, "onDownloadLoading:" + progress);
                    }
                });
            }

            @Override
            public void onApkInstall(String installPath) {
                Log.d(TAG, "onApkInstall:" + installPath);
                AppUtils.installApp(installPath);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
//        mBindDeviceRun.checkBind();
        return super.onStartCommand(intent, flags, startId);
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
        mObdReceive.onDestroy();
        mBindDeviceRun.onDestroy();
        mLocationReceive.onDestroy();
        mMediaRun.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
