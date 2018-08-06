package com.jld.obdserialport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AccReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.android.rmt.ACTION_ACC_OFF".equals(intent.getAction())) {
            Toast.makeText(context, "ACC_OFF", Toast.LENGTH_LONG).show();
        } else if ("com.android.rmt.ACTION_ACC_ON".equals(intent.getAction())) {
            Toast.makeText(context, "ACC_ON", Toast.LENGTH_LONG).show();
        }
    }
}
