package com.jld.obdserialport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jld.obdserialport.bean.response.JpushBase;
import com.jld.obdserialport.bean.response.JpushLocationBean;
import com.jld.obdserialport.bean.response.JpushMediaBean;
import com.jld.obdserialport.bean.response.UserBean;
import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.MapNaviUtils;
import com.jld.obdserialport.utils.TestLogUtil;
import com.jld.obdserialport.utils.XiaoRuiUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_BIND;

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPushReceiver";
    private Gson mGson;
    private SharedPreferences mSp;

    private static final int FLAG_NAV = 0x01;

    class MyHandler extends Handler {
        WeakReference<JPushReceiver> mReference;

        public MyHandler(JPushReceiver receiver) {
            mReference = new WeakReference<JPushReceiver>(receiver);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mReference.get() == null)
                return;
            switch (msg.what) {
                case FLAG_NAV:
                    String address = (String) msg.obj;
                    break;
            }

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
//            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

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
            Log.d(TAG, "Exception: " + e.toString());

        }
    }

    private static final int FLAG_BIND = 1;
    private static final int FLAG_UNBIND = 2;
    private static final int FLAG_NAVIGATION = 3;
    private static final int FLAG_JIEREN = 4;
    private static final int FLAG_MEDIA_REQUEST = 5;

    private void userDefinedParse(String data, Context context) {
        if (mGson == null) {
            mGson = new Gson();
            mSp = context.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
        }
//        EventBus.getDefault().post(new TestDataMessage(data));
        TestLogUtil.log(data);
        Log.d(TAG, "JpushData: " + data);
        JpushBase bean = mGson.fromJson(data, JpushBase.class);
        UserBean userBean;
        switch (bean.getFlag()) {
            case FLAG_BIND://绑定
//                mSp.edit().putString(SharedName.BIND_USER_NAME, infoBean.getNickName()).commit();
//                mSp.edit().putBoolean(SharedName.DEVICE_IS_BIND, true).apply();
                userBean = mGson.fromJson(data, UserBean.class);
                TestLogUtil.log("用户" + userBean.getNickName() + "发起绑定设备请求");
                XiaoRuiUtils.tts(context, "用户" + userBean.getNickName() + "发起绑定设备请求");
                EventBus.getDefault().post(new DefaultMessage(EVENT_MSG_BIND, ""));
                break;
            case FLAG_UNBIND://解绑
                userBean = mGson.fromJson(data, UserBean.class);
                TestLogUtil.log("用户" + userBean.getNickName() + "与设备解绑成功");
                XiaoRuiUtils.tts(context, "用户" + userBean.getNickName() + "与设备解绑成功");
//                mSp.edit().putBoolean(SharedName.DEVICE_IS_BIND, false).apply();
//                EventBus.getDefault().post(new DefaultMessage(EVENT_MSG_SHOW_BIND_CODE, ""));
                break;
            case FLAG_NAVIGATION://预约导航
            case FLAG_JIEREN://接人
                JpushLocationBean location = mGson.fromJson(data, JpushLocationBean.class);
                String address = location.getAddress();
                if (location.getFlag() == FLAG_NAVIGATION)
                    XiaoRuiUtils.tts(context, "预约导航发起成功，目的地为" + location.getAddress() + location.getSite());
                else if (location.getFlag() == FLAG_JIEREN)
                    XiaoRuiUtils.tts(context, "智能接人发起成功，目的地为" + location.getAddress() + location.getSite());
//                XiaoRuiUtils.navi(context, location.getLongitude(), location.getLatitude(), location.getAddress());
                int length = (location.getAddress() + location.getSite()).getBytes().length;
                Log.d(TAG, "AddressLength: " + length);
                int sleepTime;
                if (length <= 30)
                    sleepTime = 3000;
                else sleepTime = length * 100;
                Log.d(TAG, "sleepTime: " + sleepTime);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MapNaviUtils.isGdAutoMapInstalled()) {
                    MapNaviUtils.openAutoMap(context, location.getLatitude(),
                            location.getLongitude(), location.getSite());
                } else
                    Log.d(TAG, "未安装高德地图！！！");
                break;
            case FLAG_MEDIA_REQUEST://录制 拍照申请
//                EventBus.getDefault().post(new TestDataMessage("录制 拍照申请"));
                TestLogUtil.log("录制 拍照 申请");
//              EventBus.getDefault().post(new TestDataMessage("录制 拍照申请"));
                JpushMediaBean mediaBean = mGson.fromJson(data, JpushMediaBean.class);
                EventBus.getDefault().post(mediaBean);
//                EventBus.getDefault().post(new MediaMessage(MediaMessage.EVENT_MSG_VIDEO));
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
