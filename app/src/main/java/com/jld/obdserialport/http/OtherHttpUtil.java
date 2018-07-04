package com.jld.obdserialport.http;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


public class OtherHttpUtil extends BaseHttpUtil {

    public static final String TAG = "OtherHttpUtil";
    private static OtherHttpUtil otherHttpUtil;

    private OtherHttpUtil() {
    }

    public static OtherHttpUtil build() {
        if (otherHttpUtil == null)
            otherHttpUtil = new OtherHttpUtil();
        return otherHttpUtil;
    }

    public void checkApkUpdate(Context context){
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.d(TAG, "versionName: "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
