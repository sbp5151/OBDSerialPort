package com.jld.obdserialport.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jld.obdserialport.R;
import com.jld.obdserialport.SelfStartService;
import com.jld.obdserialport.event_msg.OBDDataMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 1、绑定SerialPortService，发送控制命令
 * 2、注册EventBus总线，接收SerialPortIMManage接收到的串口数据、显示
 */
public class TestActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "TestActivity";
    private RecyclerView mRecyclerView;
    private EditText mEtCode;
    private Button mBtn_close;
    private Button mBtn_clear;
    private ArrayList<String> mDatas = new ArrayList<>();
    private ObdDataAdapter mObdDataAdapter;
    private Handler mHandler;
    private SelfStartService.MyBinder mMyBinder;
    public static final int ADD_DATA_FLAG = 0x01;
    public static final int ADD_TEST_DATA = 0x02;
    private Spinner mSpinner;

    class MyHandler extends Handler {
        private WeakReference<TestActivity> mWeakReference;

        public MyHandler(TestActivity reference) {
            mWeakReference = new WeakReference<TestActivity>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            TestActivity reference = (TestActivity) mWeakReference.get();
            if (reference == null) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_DATA_FLAG:
                    String data = (String) msg.obj;
                    mDatas.add(data);
                    mObdDataAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mObdDataAdapter.getItemCount() - 1);
                    break;
                case ADD_TEST_DATA:
                    sendCode();
                    mHandler.sendEmptyMessageDelayed(ADD_TEST_DATA,1500);
                    break;
            }
        }
    }

    private String[] mCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_test);
        mHandler = new MyHandler(this);
        initView();
        EventBus.getDefault().register(this);
        initService();
        mCodes = getResources().getStringArray(R.array.ATCode);
    }

    private void initService() {
        Intent intent = new Intent(this, SelfStartService.class);
        startService(intent);
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: " + name);
            mMyBinder = (SelfStartService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: " + name);
        }
    };

    private void initView() {
        mEtCode = findViewById(R.id.et_code);
        mBtn_close = findViewById(R.id.btn_close);
        mBtn_close.setOnClickListener(this);
        mBtn_clear = findViewById(R.id.btn_clear);
        mBtn_clear.setOnClickListener(this);
        mSpinner = findViewById(R.id.spi_code);
        mSpinner.setOnItemSelectedListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        mRecyclerView = findViewById(R.id.rv_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mObdDataAdapter = new ObdDataAdapter(mDatas, this);
        mRecyclerView.setAdapter(mObdDataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
//            case 6:
//            case 9:
//                mEtCode.setText("=");
//                break;
//            default:
//                mEtCode.setText("");
//                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obdEvent(OBDDataMessage messageEvent) {
        if (messageEvent.getFlag() == OBDDataMessage.CONNECT_STATE_FLAG) {
            if (messageEvent.isConnect()) {
                addListData("串口连接成功！！！");
            } else {
                addListData("串口连接失败！！！");
            }
        } else if (messageEvent.getFlag() == OBDDataMessage.CONTENT_FLAG) {
            addListData(messageEvent.getMessage());
        }
    }

    private void addListData(String data) {
        Message message = mHandler.obtainMessage();
        message.what = ADD_DATA_FLAG;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                goHome();
                break;
            case R.id.btn_send:
                sendCode();
                break;
            case R.id.btn_clear:
                mDatas.clear();
                mObdDataAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 串口数据发送
     */
    private void sendCode() {
        String code = mEtCode.getText().toString();
        int position = mSpinner.getSelectedItemPosition();
//        if (position == 6 && (TextUtils.isEmpty(code) || code.equals("="))) {
//            Toast.makeText(this, "请输入PID数据", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (position == 9 && (TextUtils.isEmpty(code) || code.equals("="))) {
//            Toast.makeText(this, "请输入需要修改的总里程", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (mMyBinder == null) {
            Toast.makeText(this, "服务器未连接！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mMyBinder.isConnect()) {
            Toast.makeText(this, "串口未连接！！！", Toast.LENGTH_SHORT).show();
            return;
        }
//        mMyBinder.sendData(mCodes[position] + code);
        if (position == 6)
            mMyBinder.sendData("ATBUD="+code);
        else
            mMyBinder.sendData(mCodes[position]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    private void goHome() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goHome();
            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }
}
