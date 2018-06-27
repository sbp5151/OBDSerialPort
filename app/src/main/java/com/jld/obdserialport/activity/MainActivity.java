package com.jld.obdserialport.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.obdserialport.R;
import com.jld.obdserialport.SelfStartService;
import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.test.TestActivity;
import com.jld.obdserialport.utils.DeviceUtils;
import com.jld.obdserialport.utils.EncryptUtils;
import com.jld.obdserialport.utils.ZxingUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private int mClickNum = 0;
    private TextView mTvBindMessage;
    private ImageView mIvBindCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        initView();
        //启动后台服务
        Intent serviceIntent = new Intent(this, SelfStartService.class);
        startService(serviceIntent);

        //开启测试界面
        Intent activityIntent = new Intent(MainActivity.this, TestActivity.class);
        startActivity(activityIntent);

        EventBus.getDefault().register(this);
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
        mIvBindCode.setImageBitmap(ZxingUtil.createBitmap(EncryptUtils.encryptMD5ToString(DeviceUtils.getMacAddress())));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obdEvent(DefaultMessage defaultMessage) {
        if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_SHOW_CODE) {
            mIvBindCode.setVisibility(View.VISIBLE);
            mTvBindMessage.setText(getString(R.string.init_succeed_hind));
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_HIDE_CODE) {
            mIvBindCode.setVisibility(View.INVISIBLE);
        } else if (defaultMessage.getFlag() == DefaultMessage.EVENT_MSG_NETWORK_ERROR) {
            mTvBindMessage.setText(getString(R.string.network_error));
        }
    }
}
