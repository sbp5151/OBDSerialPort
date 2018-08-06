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
        //开机启动串口数据读取service
        if ("com.rmt.action.INSTALL_RESULT".equals(intent.getAction())) {
            String package_name = intent.getStringExtra("package_name");
            if (package_name.equals(context.getPackageName())) {
                Intent serviceIntent = new Intent(context, SelfStartService.class);
                context.startService(serviceIntent);
            }
        } else if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, SelfStartService.class);
            context.startService(serviceIntent);
        }
    }
}
