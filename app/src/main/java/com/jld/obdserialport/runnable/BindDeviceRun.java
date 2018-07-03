package com.jld.obdserialport.runnable;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jld.obdserialport.R;
import com.jld.obdserialport.bean.BaseBean;
import com.jld.obdserialport.bean.BindMsgBean;
import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.http.BaseHttpUtil;
import com.jld.obdserialport.http.BindHttpUtil;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.SharedName;
import com.jld.obdserialport.utils.ZxingUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static android.content.Context.MODE_PRIVATE;
import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_UNBIND;
import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_NETWORK_ERROR;
import static com.jld.obdserialport.event_msg.DefaultMessage.EVENT_MSG_BIND;

public class BindDeviceRun implements TagAliasCallback {

    private static final String TAG = "BindDeviceRun";
    //点击超时
    private static final int MSG_CLICK_TIMEOUT = 0x01;
    //设置JPush别名
    private static final int MSG_SET_ALIAS = 0x02;
    //上传JPush信息
    private static final int MSG_UPLOAD_JPUSH_MEG = 0x03;
    //请求绑定信息
    private static final int MSG_REQUEST_BIND_MEG = 0x04;
    //Toast
    private static final int MSG_TOAST = 0x05;
    //上传设备ID
    private static final int MSG_UPLOAD_DEVICE_ID = 0x06;
    //隐藏绑定二维码
    private static final int MSG_HIED_BIND_CODE = 0x07;
    //显示绑定二维码
    private static final int MSG_SHOW_BIND_CODE = 0x08;
    //code倒计时
    public static final int MSG_CODE_COUNT_DOWN = 0x09;

    private boolean mCountDownPause = false;
    private int mCodeShotTime = 180;
    private Context mContext;
    private SharedPreferences mSp;
    private Gson mGson;
    private MyHandler mHandler;
    private final EventBus mEventBus;
    private Button mBtn_close;
    private Dialog mBindDialog;

    private class MyHandler extends Handler {
        private WeakReference<BindDeviceRun> mWeakReference;

        public MyHandler(BindDeviceRun run) {
            mWeakReference = new WeakReference<>(run);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case MSG_CLICK_TIMEOUT:
                    break;
                case MSG_SET_ALIAS:
                    Log.d(TAG, "设置JPush别名...");
                    mySendMessage("设置JPush别名...");
                    JPushInterface.setAliasAndTags(mContext.getApplicationContext(),
                            Constant.JPUSH_DEVICE_ALIAS,
                            null,
                            BindDeviceRun.this);
                    break;
                case MSG_UPLOAD_JPUSH_MEG:
                    mySendMessage("上传JPush绑定信息...");
                    Log.d(TAG, "上传JPush绑定信息...");
                    BindHttpUtil.build().jPushBindUpload(MSG_UPLOAD_JPUSH_MEG, Constant.JPUSH_DEVICE_ALIAS, new HttpCallback());
                    break;
                case MSG_REQUEST_BIND_MEG:
                    mySendMessage("获取JPush绑定信息...");
                    Log.d(TAG, "获取JPush绑定信息...");
                    BindHttpUtil.build().jPushBindRequest(MSG_REQUEST_BIND_MEG, new HttpCallback());
                    break;
                case MSG_UPLOAD_DEVICE_ID:
                    mySendMessage("上传设备ID...");
                    Log.d(TAG, "上传设备ID...");
                    BindHttpUtil.build().uploadDeviceID(MSG_UPLOAD_DEVICE_ID, new HttpCallback());
                    break;
                case MSG_TOAST:
                    Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_HIED_BIND_CODE:
                    mEventBus.post(new DefaultMessage(EVENT_MSG_BIND, ""));
                    break;
                case MSG_SHOW_BIND_CODE:
//                    if (mSp.getBoolean(SharedName.JPUSH_MSG_IS_UPLOAD, false)) {
                        mEventBus.post(new DefaultMessage(EVENT_MSG_UNBIND, ""));
                        showBindDialog();
//                    } else {
//                        mySendMessage("JPush信息没有上传，不能显示二维码，15秒后再判断...");
//                        Log.i(TAG, "JPush信息没有上传，不能显示二维码，15秒后再判断");
//                        mHandler.sendEmptyMessageDelayed(MSG_SHOW_BIND_CODE, 1000 * 15);
//                    }
                    break;
                case MSG_CODE_COUNT_DOWN://二维码倒计时
                    if (!mCountDownPause) {
                        mCodeShotTime--;
                        mBtn_close.setText(mContext.getString(R.string.bind_code_close) + "(" + mCodeShotTime + ")");
                    }
                    if (mCodeShotTime == 0) {
                        if (mBindDialog != null && mBindDialog.isShowing())
                            mBindDialog.dismiss();
                    } else
                        mHandler.sendEmptyMessageDelayed(MSG_CODE_COUNT_DOWN, 1000);
                    break;
            }
        }
    }

    public BindDeviceRun(Context context) {
        Log.d(TAG, "BindDeviceRun");
        mContext = context;
        mSp = mContext.getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        mGson = new Gson();
        mHandler = new MyHandler(this);
        mEventBus = EventBus.getDefault();
    }

    public void checkBind() {
        // 设备ID上传
        if (!mSp.getBoolean(SharedName.DEVICE_IS_UPLOAD, false) && !TextUtils.isEmpty(Constant.OBD_DEFAULT_ID))
            mHandler.sendEmptyMessage(MSG_UPLOAD_DEVICE_ID);
        //极光推送注册别名
        if (!mSp.getBoolean(SharedName.JPUSH_SETALIAS_SUCCEED, false))
            mHandler.sendEmptyMessage(MSG_SET_ALIAS);
        else if (!mSp.getBoolean(SharedName.JPUSH_MSG_IS_UPLOAD, false))
            mHandler.sendEmptyMessage(MSG_UPLOAD_JPUSH_MEG);
        else
            mHandler.sendEmptyMessage(MSG_SHOW_BIND_CODE);
        //获取绑定信息
        if (!mSp.getBoolean(SharedName.DEVICE_IS_BIND, false))
            mHandler.sendEmptyMessage(MSG_REQUEST_BIND_MEG);
    }

    private void showBindDialog() {
        mCodeShotTime = 180;
        mBindDialog = new Dialog(mContext, R.style.CustomDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.bind_dialog, null);
        ImageView bind_code = view.findViewById(R.id.iv_bind_code);
        Button btn_pause = view.findViewById(R.id.btn_pause);
        mBtn_close = view.findViewById(R.id.btn_close);
        mBtn_close.setText(mContext.getString(R.string.bind_code_close) + "(" + mCodeShotTime + ")");
        bind_code.setImageBitmap(ZxingUtil.createBitmap("http://www.futurevi.com/download.html?sn=" + Constant.OBD_DEFAULT_ID));
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCountDownPause = true;
                mHandler.removeMessages(MSG_CODE_COUNT_DOWN);
            }
        });
        mBtn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBindDialog.dismiss();
            }
        });
        mBindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        mBindDialog.setContentView(view);
        mBindDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mBindDialog.setCancelable(false);
        mBindDialog.setCanceledOnTouchOutside(false);
        mBindDialog.show();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_COUNT_DOWN, 1000);
    }

    public void onDestroy() {
        mHandler.removeMessages(MSG_UPLOAD_JPUSH_MEG);
        mHandler.removeMessages(MSG_CODE_COUNT_DOWN);
    }

    private void mySendMessage(String message) {
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, message));
    }

    public void toast(String msg) {
        Message message = mHandler.obtainMessage();
        message.obj = msg;
        message.what = MSG_TOAST;
        mHandler.sendMessage(message);
    }

    @Override
    public void gotResult(int code, String s, Set<String> set) {
        switch (code) {
            case 0:
                mySendMessage("JPush别名设置成功");
                Log.i(TAG, "JPush别名设置成功");
                mSp.edit().putBoolean(SharedName.JPUSH_SETALIAS_SUCCEED, true).apply();
                mHandler.sendEmptyMessage(MSG_UPLOAD_JPUSH_MEG);
                break;
            case 6002:
                mySendMessage("JPush30s重复申请");
                Log.i(TAG, "JPush30s重复申请");
                mHandler.sendEmptyMessageDelayed(MSG_SET_ALIAS, 1000 * 30);
                break;
            default:
                mySendMessage("JPush设置别名失败 errorCode:" + code);
                Log.e(TAG, "JPush设置别名失败 errorCode:" + code);
        }
    }

    class HttpCallback implements BaseHttpUtil.MyCallback {

        @Override
        public void onFailure(int tag, String errorMessage) {

            switch (tag) {
                case MSG_UPLOAD_JPUSH_MEG:
                    Log.d(TAG, "上传JPush绑定信息失败，15s后再次上传 errorMessage=" + errorMessage);
                    mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_JPUSH_MEG, 1000 * 15);
                    break;
                case MSG_REQUEST_BIND_MEG:
                    Log.d(TAG, "获取JPush绑定信息失败，15s后再次获取 errorMessage=" + errorMessage);
                    mHandler.sendEmptyMessageDelayed(MSG_REQUEST_BIND_MEG, 1000 * 15);
                    break;
                case MSG_UPLOAD_DEVICE_ID:
                    Log.d(TAG, "上传设备ID失败，15s后再次上传 errorMessage=" + errorMessage);
                    mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_DEVICE_ID, 1000 * 15);
                    break;
            }
            toast(mContext.getString(R.string.network_error));
            mEventBus.post(new DefaultMessage(EVENT_MSG_NETWORK_ERROR, ""));
        }

        @Override
        public void onResponse(int tag, String body) {
            BaseBean baseBean;
            switch (tag) {
                case MSG_UPLOAD_JPUSH_MEG:
                    baseBean = mGson.fromJson(body, BaseBean.class);
                    if (baseBean.getResult() == 0) {
                        Log.d(TAG, "JPush绑定信息上传成功 body=" + body);
                        mSp.edit().putBoolean(SharedName.JPUSH_MSG_IS_UPLOAD, true).apply();
                        mHandler.sendEmptyMessage(MSG_SHOW_BIND_CODE);
                    } else {
                        Log.d(TAG, "JPush绑定信息上传失败 10s后再次上传 msg=" + body);
                        mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_JPUSH_MEG, 1000 * 10);
                    }
                    break;
                case MSG_REQUEST_BIND_MEG:
                    BindMsgBean bindMsgBean = mGson.fromJson(body, BindMsgBean.class);
                    if (bindMsgBean.getResult() == 0) {
                        Log.d(TAG, "获取JPush绑定信息成功  body=" + body);
                        if (bindMsgBean.getIsBinding() == 0) {//未绑定
//                            mHandler.sendEmptyMessage(MSG_SHOW_BIND_CODE);
                        } else if (bindMsgBean.getIsBinding() == 1) {//已绑定
//                            mHandler.sendEmptyMessage(MSG_HIED_BIND_CODE);
                        }
                    } else {
                        Log.d(TAG, "获取JPush绑定信息失败 10s后再次获取 msg=" + body);
                        mHandler.sendEmptyMessageDelayed(MSG_REQUEST_BIND_MEG, 1000 * 10);
                    }
                    break;
                case MSG_UPLOAD_DEVICE_ID:
                    baseBean = mGson.fromJson(body, BaseBean.class);
                    if (baseBean.getResult() == 0) {
                        Log.d(TAG, "上传设备ID成功 body=" + body);
                        mSp.edit().putBoolean(SharedName.DEVICE_IS_UPLOAD, true).apply();
                    } else
                        Log.d(TAG, "上传设备ID失败 msg=" + body);
                    break;
            }
        }
    }
}
