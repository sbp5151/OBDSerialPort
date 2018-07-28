package com.jld.obdserialport;

import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.jld.obdserialport.util.PhoneUtils;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    public static String OBD_DEFAULT_ID;
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
        OBD_DEFAULT_ID = PhoneUtils.getIMEI();
        JPUSH_DEVICE_ALIAS  = OBD_DEFAULT_ID;
    }
}
