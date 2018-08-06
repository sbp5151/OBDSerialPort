package com.jld.obdserialport;

import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.jld.obdserialport.util.PhoneUtils;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    public static String OBD_ID;
    /**
     * 极光推送别名
     */
    public static String JPUSH_DEVICE_ALIAS;

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        OBD_ID = PhoneUtils.getIMEI();
//        OBD_ID = "Future_V1.0_08888";
        JPUSH_DEVICE_ALIAS = OBD_ID;
    }
}
