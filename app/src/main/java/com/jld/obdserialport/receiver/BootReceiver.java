package com.jld.obdserialport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jld.obdserialport.SelfStartService;

/**
 * 开机启动SerialPortService
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        //开机启动串口数据读取service
        Intent serviceIntent = new Intent(context, SelfStartService.class);
        context.startService(serviceIntent);
    }
}
