package com.jld.obdserialport.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jld.obdserialport.R;
import com.jld.obdserialport.SelfStartService;
import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.test.TestActivity;
import com.jld.obdserialport.util.AppUtils;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.SharedName;
import com.jld.obdserialport.utils.ZxingUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private int mClickNum = 0;
    private TextView mTvBindMessage;
    private ImageView mIvBindCode;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        initView();
        EventBus.getDefault().register(this);
        mSp = getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        //启动后台服务
        Intent serviceIntent = new Intent(this, SelfStartService.class);
        startService(serviceIntent);
    }

    private void initView() {
        mTvBindMessage = findViewById(R.id.tv_bind_message);
        mIvBindCode = findViewById(R.id.iv_bind_code);
        mIvBindCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickNum++;
                if (mClickNum == 6) {
                    mClickNum = 0;
                    Intent intent = new Intent(MainActivity.this, TestActivity.class);
                    startActivity(intent);
                }
            }
        });
        mIvBindCode.setImageBitmap(ZxingUtil.createBitmap("http://www.futurevi.com/download.html?sn=" + Constant.OBD_DEFAULT_ID));
        mIvBindCode.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obdEvent(DefaultMessage defaultMessage) {
        Log.d(TAG, "obdEvent: " + defaultMessage);
        if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_BIND) {//已绑定
            bindState();
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_UNBIND) {//未绑定
            unBindState();
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_NETWORK_ERROR) {//网络错误
            networkErrorState();
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_BINDING) {//绑定中
            bindingState();
        }
    }

    //绑定状态
    private void bindState() {
        String userName = mSp.getString(SharedName.BIND_USER_NAME, "");
        String userNameHint = getString(R.string.bind_user_name);
        mTvBindMessage.setText(userName + userNameHint);
    }

    private void unBindState() {
        mIvBindCode.setVisibility(View.VISIBLE);
        mTvBindMessage.setText(getString(R.string.bind_hind));
    }

    private void networkErrorState() {
        mIvBindCode.setVisibility(View.GONE);
        mTvBindMessage.setText(getString(R.string.network_error));
    }

    private void bindingState() {
        mIvBindCode.setVisibility(View.GONE);
        mTvBindMessage.setText(getString(R.string.initing_hint));
    }
}
