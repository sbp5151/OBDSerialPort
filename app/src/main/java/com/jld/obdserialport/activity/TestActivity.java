package com.jld.obdserialport.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jld.obdserialport.MyApplication;
import com.jld.obdserialport.R;
import com.jld.obdserialport.SelfStartService;
import com.jld.obdserialport.event_msg.TestDataMessage;
import com.jld.obdserialport.http.FileHttpUtil;
import com.jld.obdserialport.utils.TestLogUtil;
import com.jld.obdserialport.utils.XiaoRuiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
    private Gson mGson;

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
                    addData(data);
                    break;
                case ADD_TEST_DATA:
                    sendCode();
                    mHandler.sendEmptyMessageDelayed(ADD_TEST_DATA, 1500);
                    break;
            }
        }
    }

    private synchronized void addData(String data) {
        mDatas.add(data);
        mObdDataAdapter.notifyDataSetChanged();
        if (isVisBottom(mRecyclerView))
            mRecyclerView.scrollToPosition(mObdDataAdapter.getItemCount() - 1);
    }

    private boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        int childCount = layoutManager.getChildCount();
        int itemCount = layoutManager.getItemCount();
        int state = recyclerView.getScrollState();
        if (state == RecyclerView.SCROLL_STATE_IDLE && childCount > 0 && lastVisibleItem >= (itemCount - 5))
            return true;
        else return false;
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
    public void obdEvent(TestDataMessage messageEvent) {
        String testMessage = messageEvent.getTestMessage();
        if (!TextUtils.isEmpty(testMessage))
            addListData(messageEvent.getTestMessage());

//        if (messageEvent.getFlag() == OBDDataMessage.CONNECT_STATE_FLAG) {
//            if (messageEvent.isConnect()) {
//                addListData("串口连接成功！！！");
//            } else {
//                addListData("串口连接失败！！！");
//            }
//        } else if (messageEvent.getFlag() == OBDDataMessage.CONTENT_FLAG) {
//            addListData(messageEvent.getMessage());
//        }
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
//                if (mGson == null)
//                    mGson = new Gson();
//
//                String mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "videotest.3gp";
//                EventBus.getDefault().post(new TestDataMessage("视频上传："+mVideoPath));
//
//                FileHttpUtil.build().videoUploadUtil(MyApplication.OBD_ID, "", "", mVideoPath, new FileHttpUtil.UploadFileListener() {
//                    @Override
//                    public void onUploadFailed(String eroMessage) {
//                        Log.d(TAG, "onUploadFailed: " + eroMessage);
////                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_UPLOAD, 1000 * 10);
//                        EventBus.getDefault().post(new TestDataMessage("视频上传失败 10s后继续上传:" + eroMessage));
//                    }
//
//                    @Override
//                    public void onUploadSucceed() {
//                        EventBus.getDefault().post(new TestDataMessage("视频上传成功"));
//                    }
//                });
//                JpushMediaBean mediaBean = mGson.fromJson("{\"fileName\":\"g8s647g8a6hawj008fjiw9udi1532746474975\",\"fileType\":2,\"flag\":5,\"obdId\":\"866275038851383\",\"uid\":\"olBG94sMevoxx0UZ0CwhVdBiaOCQ\",\"videoDuration\":1}", JpushMediaBean.class);
//                Log.d(TAG, "mediaBean: "+mediaBean);
//                String mVideoPath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "future.3gp";
//                FileHttpUtil.build().videoUploadUtil(MyApplication.OBD_ID, "1323", "42344", mVideoPath2, new FileHttpUtil.UploadFileListener() {
//                    @Override
//                    public void onUploadFailed(String eroMessage) {
//                        Log.d(TAG, "onUploadFailed: " + eroMessage);
////                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_UPLOAD, 1000 * 10);
////                        EventBus.getDefault().post(new TestDataMessage());
//                        TestLogUtil.log("视频上传失败 10s后继续上传:" + eroMessage);
//                    }
//
//                    @Override
//                    public void onUploadSucceed(String msg) {
//                        TestLogUtil.log("视频上传成功:" + msg);
////                        EventBus.getDefault().post(new TestDataMessage("视频上传成功"));
//                    }
//                });
//                goHome();
                finish();
//                EventBus.getDefault().post(mediaBean);
                break;
            case R.id.btn_send:
//                EventBus.getDefault().post(new JpushMediaBean(2));
//                if (mGson == null)
//                    mGson = new Gson();
//                JpushBase jpushBase = mGson.fromJson("{\"fileName\":\"x4lkr1y909jv78lnofofg8pvi1532681646138\",\"fileType\":2,\"flag\":5,\"obdId\":\"866275038851383\",\"uid\":\"olBG94sMevoxx0UZ0CwhVdBiaOCQ\",\"videoDuration\":1}", JpushBase.class);
//                Log.d(TAG, "jpushBase: "+jpushBase);
                sendCode();
//                String mVideoPath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "future.mp4";
//                FileHttpUtil.build().videoUploadUtil(MyApplication.OBD_ID, "132", "4234", mVideoPath1, new FileHttpUtil.UploadFileListener() {
//                    @Override
//                    public void onUploadFailed(String eroMessage) {
//                        Log.d(TAG, "onUploadFailed: " + eroMessage);
////                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_UPLOAD, 1000 * 10);
////                        EventBus.getDefault().post(new TestDataMessage("视频上传失败 10s后继续上传:" + eroMessage));
//                        TestLogUtil.log("视频上传失败 10s后继续上传:" + eroMessage);
//                    }
//
//                    @Override
//                    public void onUploadSucceed(String msg) {
////                        EventBus.getDefault().post(new TestDataMessage("视频上传成功"));
//                        TestLogUtil.log("视频上传成功:" + msg);
//                    }
//                });
                break;
            case R.id.btn_clear:
//                String compressPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "future.3gp";
//                try {
//                    videoCompress(compressPath);
//                } catch (FFmpegCommandAlreadyRunningException e) {
//                    e.printStackTrace();
//                } catch (FFmpegNotSupportedException e) {
//                    e.printStackTrace();
//                }
                mDatas.clear();
                mObdDataAdapter.notifyDataSetChanged();
//                String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "apps" + File.separator + "future_803_2.apk";
//                TestLogUtil.log("apkPath:" + apkPath);
//                if (new File(apkPath).exists())
//                    XiaoRuiUtils.silentAppInstall(this, apkPath);
//                else TestLogUtil.log(apkPath + "不存在");
////                XiaoRuiUtils.tts(this,"测试tts语音播报");
//                break;
        }
    }


//    private void videoCompress(String videoPath) throws FFmpegCommandAlreadyRunningException, FFmpegNotSupportedException {
//        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
//        LocalMediaConfig config = buidler.setVideoPath(videoPath)
//                .captureThumbnailsTime(1)
//                .doH264Compress(new AutoVBRMode())
//                .setFramerate(15)
//                .build();
//        OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();

//        try {
//            String newVideoPath = SiliCompressor.with(this).compressVideo(videoPath,
//                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG).getPath());
//            TestLogUtil.log("videoCompress: "+newVideoPath);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "futureCompress.3gp";
//        String[] commands = new String[]{"-threads", "1", "-i", videoPath, "-c:v", "libx264", "-crf", "30", "-preset", "superfast", "-y", "-acodec", "libmp3lame", savePath};
//        String[] command = new String[]{"-i", videoPath};
//        FFmpeg fFmpeg = FFmpeg.getInstance(this);
//        fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
//            @Override
//            public void onFailure() {
//                TestLogUtil.log("加载失败 onFailure: ");
//
//            }
//
//            @Override
//            public void onSuccess() {
//                TestLogUtil.log("加载成功 onSuccess: ");
//
//            }
//
//            @Override
//            public void onStart() {
//                TestLogUtil.log("加载开始 onStart: ");
//
//            }
//
//            @Override
//            public void onFinish() {
//                TestLogUtil.log("加载完成 onFinish: ");
//
//            }
//        });
//        fFmpeg.execute(commands, new FFmpegExecuteResponseHandler() {
//            @Override
//            public void onSuccess(String message) {
//                TestLogUtil.log("压缩成功 onSuccess: " + message);
//            }
//
//            @Override
//            public void onProgress(String message) {
//                TestLogUtil.log("压缩 onProgress: " + message);
//            }
//
//            @Override
//            public void onFailure(String message) {
//                TestLogUtil.log("压缩失败 onFailure: " + message);
//            }
//
//            @Override
//            public void onStart() {
//                TestLogUtil.log("压缩开始 onStart");
//            }
//
//            @Override
//            public void onFinish() {
//                TestLogUtil.log("压缩完成 onFinish");
//            }
//        });
//    }

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
            mMyBinder.sendData("ATBUD=" + code);
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
