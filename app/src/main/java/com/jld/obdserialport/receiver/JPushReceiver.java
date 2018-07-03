package com.jld.obdserialport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jld.obdserialport.bean.UserInfoBean;
import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.MapNaviUtils;
import com.jld.obdserialport.utils.SharedName;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_BINDING;
import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_BIND;
import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_UNBIND;

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPushReceiver";
    private Gson mGson;
    private SharedPreferences mSp;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//                processCustomMessage(context, bundle);
                userDefinedParse(bundle.getString(JPushInterface.EXTRA_MESSAGE), context);
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
//                //打开自定义的Activity
//                Intent i = new Intent(context, TestActivity.class);
//                i.putExtras(bundle);
//                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }
    }

    private void userDefinedParse(String data, Context context) {
        if (mGson == null) {
            mGson = new Gson();
            mSp = context.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
        }
        UserInfoBean infoBean = mGson.fromJson(data, UserInfoBean.class);
        Log.d(TAG, "infoBean: " + infoBean);
        switch (infoBean.getFlag()) {
            case 1://绑定
                mSp.edit().putString(SharedName.BIND_USER_NAME,infoBean.getNickName()).commit();
                mSp.edit().putBoolean(SharedName.DEVICE_IS_BIND, true).apply();
                EventBus.getDefault().post(new DefaultMessage(EVENT_MSG_BIND, ""));
                break;
            case 2://解绑
                mSp.edit().putBoolean(SharedName.DEVICE_IS_BIND, false).apply();
                EventBus.getDefault().post(new DefaultMessage(EVENT_MSG_UNBIND, ""));
                break;
            case 3://预约导航
                if (MapNaviUtils.isGdAutoMapInstalled()) {
                    MapNaviUtils.openAutoMap(context, infoBean.getLatitude(),
                            infoBean.getLongitude(), infoBean.getSite());
                } else
                    Log.d(TAG, "未安装高德地图！！！");

                break;
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

}
