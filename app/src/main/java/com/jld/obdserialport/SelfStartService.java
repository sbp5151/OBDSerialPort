package com.jld.obdserialport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.jld.obdserialport.http.FileHttpUtil;
import com.jld.obdserialport.http.OtherHttpUtil;
import com.jld.obdserialport.runnable.BindDeviceRun;
import com.jld.obdserialport.runnable.DeviceRun;
import com.jld.obdserialport.runnable.LocationReceiveRun;
import com.jld.obdserialport.runnable.MediaRun;
import com.jld.obdserialport.runnable.OBDReceiveRun;
import com.jld.obdserialport.util.AppUtils;
import com.jld.obdserialport.utils.TestLogUtil;
import com.jld.obdserialport.utils.XiaoRuiUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 1、启动三大runnable
 * 2、与TestActivity进行数据通信
 */
public class SelfStartService extends Service {

    private static final String TAG = "SelfStartService";
    private MyBinder mMyBinder;
    public LocationReceiveRun mLocationRun;
    private OBDReceiveRun mObdRun;
    private BindDeviceRun mBindDeviceRun;
    private MediaRun mMediaRun;
    private DeviceRun mDeviceRun;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        //极光绑定任务
        mBindDeviceRun = new BindDeviceRun(this);
        //OBD数据获取任务
        mObdRun = new OBDReceiveRun(this);
        //开启GPS信息获取任务
        mLocationRun = new LocationReceiveRun(this);
        //开启媒体任务
        mMediaRun = new MediaRun(this);
        //开启设备更新任务
        mDeviceRun = new DeviceRun();
        wifiEnable();
        apkUpdateCheck();
    }

    //APK更新检查
    private void apkUpdateCheck() {
        OtherHttpUtil.build().checkApkUpdate(this, new OtherHttpUtil.ApkCheckUpdateListener() {
            @Override
            public void onApkDownload(String download) {
                String apkName = download.substring(download.lastIndexOf("/") + 1).replace(".1", "");
                final File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CarFuture" + File.separator + apkName);
                FileHttpUtil.build().fileDownload(download, new FileHttpUtil.DownloadFileListener() {
                    @Override
                    public void onDownloadFailed() {
                        Log.d(TAG, "onDownloadFailed");
                        TestLogUtil.log("onDownloadFailed");
                    }

                    @Override
                    public void onDownloadSucceed() {
                        TestLogUtil.log("onDownloadSucceed:" + saveFile);
                        Log.d(TAG, "onDownloadSucceed:" + saveFile);
//                        AppUtils.installApp(saveFile);
                        if (saveFile.exists())
                            XiaoRuiUtils.silentAppInstall(SelfStartService.this, saveFile.getAbsolutePath());
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
                TestLogUtil.log("onApkInstall:" + installPath);
                XiaoRuiUtils.silentAppInstall(SelfStartService.this, installPath);
            }
        });
    }

    private void wifiEnable() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
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
            mObdRun.addWriteData(data);
        }

        public boolean isConnect() {
            return mObdRun.isConnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy:");
        mObdRun.onDestroy();
        mBindDeviceRun.onDestroy();
        mLocationRun.onDestroy();
        mMediaRun.onDestroy();
        mDeviceRun.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
