package com.jld.obdserialport.runnable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.jld.obdserialport.event_msg.DefaultMessage;
import com.jld.obdserialport.event_msg.MediaMessage;
import com.jld.obdserialport.http.FileHttpUtil;
import com.jld.obdserialport.utils.Constant;

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
    private static final String PHOTO_REQUEST_ACTION = "com.media.action.Future_Request_Photo";
    //录制返回广播action
    private static final String VIDEO_RETURN_ACTION = "com.media.action.Future_Return_Video";
    //录制请求广播action
    private static final String VIDEO_REQUEST_ACTION = "com.media.action.Future_Request_Video";

    private static final int FLAG_PHOTO_UPLOAD = 0x01;
    private static final int FLAG_VIDEO_UPLOAD = 0x02;
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
                    FileHttpUtil.build().uploadFileUtil(Constant.URL_PHTO_UPLOAD, mPhotoPath, new FileHttpUtil.UploadFileListener() {
                        @Override
                        public void onUploadFailed() {
                            mHandler.sendEmptyMessageDelayed(FLAG_PHOTO_UPLOAD, 1000 * 5);
                        }
                    });
                    break;
                case FLAG_VIDEO_UPLOAD://视频上传
                    FileHttpUtil.build().uploadFileUtil(Constant.URL_VIDOA_UPLOAD, mVideoPath, new FileHttpUtil.UploadFileListener() {
                        @Override
                        public void onUploadFailed() {
                            mHandler.sendEmptyMessageDelayed(FLAG_PHOTO_UPLOAD, 1000 * 5);
                        }
                    });
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
        EventBus.getDefault().register(this);
        mMediaReceiver = new MediaReceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mediaEvent(MediaMessage message) {
        Log.d(TAG, "mediaEvent: " + message);
        Intent intent = new Intent();
        switch (message.getFlag()) {
            case MediaMessage.EVENT_MSG_PHOTO://拍照请求
                intent.setAction(PHOTO_REQUEST_ACTION);
                mContext.sendBroadcast(intent);
                break;
            case MediaMessage.EVENT_MSG_VIDEO://录像请求
                intent.setAction(VIDEO_REQUEST_ACTION);
                intent.putExtra("videoDuration",10);
                mContext.sendBroadcast(intent);
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
                mPhotoPath = intent.getStringExtra("photoPath");
                if (!TextUtils.isEmpty(mPhotoPath))
                    mHandler.sendEmptyMessage(FLAG_PHOTO_UPLOAD);
            } else if (VIDEO_RETURN_ACTION.equals(intent.getAction())) {//录制返回
                mVideoPath = intent.getStringExtra("videoPath");
                if (!TextUtils.isEmpty(mVideoPath))
                    mHandler.sendEmptyMessage(FLAG_VIDEO_UPLOAD);
            }
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(FLAG_PHOTO_UPLOAD);
        mHandler.removeMessages(FLAG_VIDEO_UPLOAD);
        mContext.unregisterReceiver(mMediaReceiver);
    }
}
