package com.jld.obdserialport.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.obdserialport.MyApplication;
import com.jld.obdserialport.R;
import com.jld.obdserialport.SelfStartService;
import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.SharedName;
import com.jld.obdserialport.utils.ZxingUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.jld.obdserialport.MyApplication.OBD_ID;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private int mClickNum = 0;
    private TextView mTvBindMessage;
    private TextView mTvBindImei;
    private ImageView mIvBindCode;
    private SharedPreferences mSp;
    private long mLastChangeTime;
    private NetworkReceive mNetworkReceive;
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        mSp = getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        mNetworkReceive = new NetworkReceive();
        initView();
        if (mSp.getBoolean(SharedName.JPUSH_MSG_IS_UPLOAD, false))
            showBindCode();
        else
            initStatus();
        EventBus.getDefault().register(this);
        //启动后台服务
        Intent serviceIntent = new Intent(this, SelfStartService.class);
        startService(serviceIntent);
        Log.d(TAG, "OBD_ID: " + MyApplication.OBD_ID);
        for (int i = 0; permissions!=null && i < permissions.length;i++){

        }
    }

    private void initView() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        sendBroadcast(intent,null);
        mTvBindImei = findViewById(R.id.tv_bind_imei);
        mTvBindImei.setText(OBD_ID);
        mTvBindMessage = findViewById(R.id.tv_bind_message);
        mIvBindCode = findViewById(R.id.iv_bind_code);
        TextView tv_version = findViewById(R.id.tv_version);
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(versionName))
            tv_version.setText("V" + versionName);
        findViewById(R.id.ll_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickNum++;
//                EventBus.getDefault().post(new MediaMessage(MediaMessage.EVENT_MSG_PHOTO));
                if (mClickNum == 6) {
                    mClickNum = 0;
                    Intent intent = new Intent(MainActivity.this, TestActivity.class);
                    startActivity(intent);
                }
            }
        });
        mIvBindCode.setImageBitmap(ZxingUtil.createBitmap("http://www.futurevi.com/download.html?sn=" + OBD_ID));
    }

    private void toSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obdEvent(DefaultMessage defaultMessage) {
        Log.d(TAG, "obdEvent: " + defaultMessage);
        if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_BIND) {//已绑定
//            bindState();
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_SHOW_BIND_CODE) {//未绑定
            showBindCode();
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_NETWORK_ERROR) {//网络错误
            networkErrorState();
        }
    }


    //    //绑定状态
//    private void bindState() {
//        String userName = mSp.getString(SharedName.BIND_USER_NAME, "");
//        String userNameHint = getString(R.string.bind_user_name);
//        mIvBindCode.setVisibility(View.VISIBLE);
//        mTvBindMessage.setText(userName + userNameHint);
//    }

    private void showBindCode() {
        mIvBindCode.setVisibility(View.VISIBLE);
        mTvBindMessage.setText(getString(R.string.bind_hind));
    }

    private void networkErrorState() {
        mIvBindCode.setVisibility(View.GONE);
        mTvBindMessage.setText(getString(R.string.network_error));
    }

    private void initStatus() {
        mIvBindCode.setVisibility(View.GONE);
        mTvBindMessage.setText(getString(R.string.initing_hint));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class NetworkReceive extends BroadcastReceiver {

        public NetworkReceive() {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            MainActivity.this.registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            //如果无网络连接activeInfo为null
            //也可获取网络的类型
            if (activeInfo != null) { //网络连接
                if (mSp.getBoolean(SharedName.JPUSH_MSG_IS_UPLOAD, false))
                    showBindCode();
                else
                    initStatus();
//                Toast.makeText(context, "测试：网络连接成功", Toast.LENGTH_SHORT).show();
            } else { //网络断开
//                Toast.makeText(context, "测试：网络断开", Toast.LENGTH_SHORT).show();
                networkErrorState();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.unregisterReceiver(mNetworkReceive);
    }

}
