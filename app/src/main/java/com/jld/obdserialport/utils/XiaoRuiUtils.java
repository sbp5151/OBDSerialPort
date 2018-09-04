package com.jld.obdserialport.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.jld.obdserialport.http.FileHttpUtil.TAG;
import static com.jld.obdserialport.runnable.MediaRun.PHOTO_REQUEST_ACTION;
import static com.jld.obdserialport.runnable.MediaRun.VIDEO_REQUEST_ACTION;

public class XiaoRuiUtils {

    public static final String TAG = "XiaoRuiUtils";
    private static Intent ttsIntent;
    private static Intent naviIntent;
    private static Intent videoIntent;
    private static Intent picIntent;

    public static void tts(Context context, String msg) {
        if (ttsIntent == null)
            ttsIntent = new Intent("com.szcx.ecamera.tts");
        ttsIntent.putExtra("level", 9);
        ttsIntent.putExtra("tts", msg);
        context.sendBroadcast(ttsIntent);
    }

    public static void navi(Context context, double log, double lat, String poi) {
        if (naviIntent == null)
            naviIntent = new Intent("com.android.rmt.ACTION_NAVI");
        naviIntent.putExtra("log", log);
        naviIntent.putExtra("lat", lat);
        naviIntent.putExtra("poi", poi);
        context.sendBroadcast(naviIntent);
    }

    public static void takeVideo(Context context, int duration, int cameraId, String flag) {
        if (videoIntent == null)
            videoIntent = new Intent(VIDEO_REQUEST_ACTION);
        videoIntent.putExtra("duration", duration);
        videoIntent.putExtra("cameraId", cameraId);
        videoIntent.putExtra("flag", flag);
        context.sendBroadcast(videoIntent);
    }

    public static void takePic(Context context, int cameraId, String flag) {
        if (picIntent == null)
            picIntent = new Intent(PHOTO_REQUEST_ACTION);
        picIntent.putExtra("cameraId", cameraId);
        picIntent.putExtra("flag", flag);
        context.sendBroadcast(picIntent);
    }

    public static void silentAppInstall(Context context,String appPath) {
        Log.d(TAG, "silentAppInstall: "+appPath);
        Intent intent = new Intent("com.rmt.action.SILENT_INSTALL");
        intent.putExtra("apk_path", appPath);
        context.sendBroadcast(intent);
    }
}
