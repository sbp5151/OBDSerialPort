package com.jld.obdserialport.runnable;


import android.os.Handler;
import android.os.Message;

import com.jld.obdserialport.http.OtherHttpUtil;

import java.lang.ref.WeakReference;

public class DeviceRun extends BaseRun {

    private static final int FLAG_DEVICE_UPDATE = 0x01;
    //设备更新间隔时间
    public static final int DEVICE_UPDATE_INTERVAL = 1000 * 30;
    private final MyHandler mHandler;

    class MyHandler extends Handler {
        private final WeakReference<DeviceRun> mWeakReference;
        public MyHandler(DeviceRun run) {
            mWeakReference = new WeakReference<>(run);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case FLAG_DEVICE_UPDATE:
                    OtherHttpUtil.build().deviceOnlineUpdate();
                    mHandler.sendEmptyMessageDelayed(FLAG_DEVICE_UPDATE, DEVICE_UPDATE_INTERVAL);
                    break;
            }

        }
    }

    public DeviceRun() {
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessageDelayed(FLAG_DEVICE_UPDATE, 1000 * 3);
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(FLAG_DEVICE_UPDATE);
    }
}
