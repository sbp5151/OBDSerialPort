package com.jld.obdserialport.runnable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jld.obdserialport.MyApplication;
import com.jld.obdserialport.bean.MediaBean;
import com.jld.obdserialport.event_msg.MediaMessage;
import com.jld.obdserialport.http.FileHttpUtil;
import com.jld.obdserialport.utils.TestLogUtil;
import com.jld.obdserialport.utils.XiaoRuiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class MediaRun extends BaseRun {

    private static final String TAG = "MediaRun";
    private Context mContext;
    //拍照返回广播action
    private static final String PHOTO_RETURN_ACTION = "com.media.action.Future_Return_Photo";
    //拍照请求广播action
    public static final String PHOTO_REQUEST_ACTION = "com.media.action.Future_Request_Photo";
    //录制返回广播action
    private static final String VIDEO_RETURN_ACTION = "com.media.action.Future_Return_Video";
    //录制请求广播action
    public static final String VIDEO_REQUEST_ACTION = "com.media.action.Future_Request_Video";
//    //拍照返回广播action
//    private static final String PHOTO_RETURN_ACTION = "com.android.rmt.ACTION_RECEIVER_PIC";
//    //拍照请求广播action
//    private static final String PHOTO_REQUEST_ACTION = "com.android.rmt.ACTION_TAKE_PIC";
//    //录制返回广播action
//    private static final String VIDEO_RETURN_ACTION = "com.android.rmt.ACTION_RECEIVER_VIDEO";
//    //录制请求广播action
//    private static final String VIDEO_REQUEST_ACTION = "com.android.rmt.ACTION_TAKE_VIDEO";

    private static final int REQUEST_PHOTO_FLAG = 0x01;
    private static final int REQUEST_VIDEO_FLAG = 0x02;

    private static final int FLAG_PHOTO_UPLOAD = 0x03;
    private static final int FLAG_VIDEO_UPLOAD = 0x04;
    private static final int FLAG_TAKE_TIMEOUT = 0x05;
    private static final int FLAG_VIDEO_TIMEOUT = 0x06;
    private final EventBus mEventBus;

    private class MyHandler extends Handler {

        private final WeakReference<MediaRun> mMediaRunWeakReference;

        public MyHandler(MediaRun run) {

            mMediaRunWeakReference = new WeakReference<>(run);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaRunWeakReference.get() == null)
                return;
            switch (msg.what) {
                case FLAG_PHOTO_UPLOAD://相册上传
                    FileHttpUtil.build().photoUploadUtil(MyApplication.OBD_ID, mTakeUid, mTakeFileName, mPhotoPath, new FileHttpUtil.UploadFileListener() {
                        @Override
                        public void onUploadFailed(String eroMessage) {
                            Log.d(TAG, "onUploadFailed: " + eroMessage);
                            mHandler.sendEmptyMessageDelayed(FLAG_PHOTO_UPLOAD, 1000 * 10);
//                            mEventBus.post(new TestDataMessage("相册上传失败 10s后继续上传:" + eroMessage));
                            TestLogUtil.log("相册上传失败 10s后继续上传:" + eroMessage);
                        }

                        @Override
                        public void onUploadSucceed(String msg) {
//                            mEventBus.post(new TestDataMessage("相册上传成功"));
                            TestLogUtil.log("相册上传成功:" + msg);
                        }
                    });
                    break;
                case FLAG_VIDEO_UPLOAD://视频上传
//                    mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "videotest.3gp";
                    FileHttpUtil.build().videoUploadUtil(MyApplication.OBD_ID, mVideoUid, mVideoFileName, mVideoPath, new FileHttpUtil.UploadFileListener() {
                        @Override
                        public void onUploadFailed(String eroMessage) {
                            Log.d(TAG, "onUploadFailed: " + eroMessage);
                            mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_UPLOAD, 1000 * 10);
//                            mEventBus.post(new TestDataMessage("视频上传失败 10s后继续上传:" + eroMessage));
                            TestLogUtil.log("视频上传失败 10s后继续上传:" + eroMessage);

                        }

                        @Override
                        public void onUploadSucceed(String msg) {
//                            mEventBus.post(new TestDataMessage("视频上传成功"));
                            TestLogUtil.log("视频上传成功:" + msg);

                        }
                    });
                    break;
                case FLAG_TAKE_TIMEOUT:
                    mIsTake = false;
                    break;
                case FLAG_VIDEO_TIMEOUT:
                    mIsVideo = false;
                    break;
            }
        }
    }

    private String mPhotoPath;
    private String mVideoPath;
    private final MediaReceiver mMediaReceiver;
    private MyHandler mHandler;

    public MediaRun(Context context) {
        mContext = context;
        mHandler = new MyHandler(this);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mMediaReceiver = new MediaReceiver();
    }

    private boolean mIsTake = false;
    private boolean mIsVideo = false;
    private String mTakeUid;
    private String mTakeFileName;
    private String mVideoUid;
    private String mVideoFileName;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mediaEvent(MediaBean message) {
//        mEventBus.post(new TestDataMessage("收到media：" + message));
        switch (message.getFileType()) {
            case MediaMessage.EVENT_MSG_PHOTO://拍照请求
                if (!mIsTake) {
                    XiaoRuiUtils.tts(mContext, "收到拍照请求");
                    mHandler.removeMessages(FLAG_PHOTO_UPLOAD);
                    mIsTake = true;
                    mTakeUid = message.getUid();
                    mTakeFileName = message.getFileName();
//                    mEventBus.post(new TestDataMessage("请求拍照"));
                    TestLogUtil.log("请求拍照");
                    XiaoRuiUtils.takePic(mContext, 1, message.getFileName());
                    mHandler.sendEmptyMessageDelayed(FLAG_TAKE_TIMEOUT, 1000 * 3);
                }
                break;
            case MediaMessage.EVENT_MSG_VIDEO://录像请求
                if (!mIsVideo) {
                    mIsVideo = true;
                    mHandler.removeMessages(FLAG_VIDEO_UPLOAD);
                    mVideoUid = message.getUid();
                    mVideoFileName = message.getFileName();
//                    mEventBus.post(new TestDataMessage("录像请求"));
                    if (message.getVideoDuration() == 1) {
                        TestLogUtil.log("收到十秒视频录制请求");
                        XiaoRuiUtils.tts(mContext, "收到十秒视频录制请求");
                        XiaoRuiUtils.takeVideo(mContext, 1000 * 10, 1, message.getFileName());
                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_TIMEOUT, 1000 * 15);
                    } else if (message.getVideoDuration() == 2) {
                        TestLogUtil.log("收到二十秒视频录制请求");
                        XiaoRuiUtils.tts(mContext, "收到二十秒视频录制请求");
                        XiaoRuiUtils.takeVideo(mContext, 1000 * 20, 1, message.getFileName());
                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_TIMEOUT, 1000 * 25);
                    } else if (message.getVideoDuration() == 3) {
                        TestLogUtil.log("收到三十秒视频录制请求");
                        XiaoRuiUtils.tts(mContext, "收到三十秒视频录制请求");
                        XiaoRuiUtils.takeVideo(mContext, 1000 * 30, 1, message.getFileName());
                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_TIMEOUT, 1000 * 35);
                    } else
                        mHandler.sendEmptyMessageDelayed(FLAG_VIDEO_TIMEOUT, 1000 * 10);
                }
                break;
        }
    }

    class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(PHOTO_RETURN_ACTION);
            filter.addAction(VIDEO_RETURN_ACTION);
            mContext.registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (PHOTO_RETURN_ACTION.equals(intent.getAction())) {//拍照返回
//                mPhotoPath = intent.getStringExtra("photoPath");
                mPhotoPath = intent.getStringExtra("photoPath");
//                mEventBus.post(new TestDataMessage("收到拍照返回：" + mPhotoPath));
                TestLogUtil.log("收到拍照返回：" + mPhotoPath);
                Toast.makeText(mContext, "收到拍照返回：" + mPhotoPath, Toast.LENGTH_LONG).show();
                int cameraId = intent.getIntExtra("cameraId", 1);
                int status = intent.getIntExtra("status", 1);
                Log.d(TAG, "status:" + status);
                Log.d(TAG, "mPhotoPath:" + mPhotoPath);
                if (status == 0) {
                    String flag = intent.getStringExtra("flag");
                    Log.d(TAG, "flag:" + flag);
                    if (!TextUtils.isEmpty(mPhotoPath))
                        mHandler.sendEmptyMessage(FLAG_PHOTO_UPLOAD);
                }
                mIsTake = false;
            } else if (VIDEO_RETURN_ACTION.equals(intent.getAction())) {//录制返回
                mVideoPath = intent.getStringExtra("videoPath");
                int status = intent.getIntExtra("status", 1);
//                mEventBus.post(new TestDataMessage("收到录制返回：" + mVideoPath + " status:" + status));
                TestLogUtil.log("收到录制返回：" + mVideoPath + " status:" + status);
                Toast.makeText(mContext, "收到录制返回：" + mVideoPath, Toast.LENGTH_LONG).show();
                int cameraId = intent.getIntExtra("cameraId", 1);
                long time = intent.getLongExtra("this", 1);
                if (status == 1) {
                    String flag = intent.getStringExtra("flag");
                    if (!TextUtils.isEmpty(mVideoPath))
                        mHandler.sendEmptyMessage(FLAG_VIDEO_UPLOAD);
                }
                mIsVideo = false;
            }
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(FLAG_PHOTO_UPLOAD);
        mHandler.removeMessages(FLAG_VIDEO_UPLOAD);
        mContext.unregisterReceiver(mMediaReceiver);
    }
}
