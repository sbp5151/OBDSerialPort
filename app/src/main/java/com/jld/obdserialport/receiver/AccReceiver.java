package com.jld.obdserialport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jld.obdserialport.event_msg.AccMessage;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.SharedName;

import org.greenrobot.eventbus.EventBus;

public class AccReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.android.rmt.ACTION_ACC_OFF".equals(intent.getAction())) {
            Toast.makeText(context, "ACC_OFF", Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new AccMessage(AccMessage.EVENT_FLAG_ACC_OFF));
            context.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE).edit().putString(SharedName.ACC_START,"ACC_OFF").apply();
        } else if ("com.android.rmt.ACTION_ACC_ON".equals(intent.getAction())) {
            Toast.makeText(context, "ACC_ON", Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new AccMessage(AccMessage.EVENT_FLAG_ACC_ON));
            context.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE).edit().putString(SharedName.ACC_START,"ACC_ON").apply();
        }
    }
}
