package com.jld.obdserialport.runnable;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.jld.obdserialport.http.OtherHttpUtil;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.SharedName;

import java.lang.ref.WeakReference;

public class DeviceRun extends BaseRun {

    private static final int FLAG_DEVICE_UPDATE = 0x01;
    //设备更新间隔时间
    public static final int DEVICE_UPDATE_INTERVAL = 1000 * 30;
    public static final int ACCOFF_DEVICE_UPDATE_INTERVAL = 1000 * 90;
    private final MyHandler mHandler;
    private final SharedPreferences mSp;

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
                    if (mSp.getString(SharedName.ACC_START, "ACC_OFF").equals("ACC_ON"))
                        mHandler.sendEmptyMessageDelayed(FLAG_DEVICE_UPDATE, DEVICE_UPDATE_INTERVAL);
                    else
                        mHandler.sendEmptyMessageDelayed(FLAG_DEVICE_UPDATE, ACCOFF_DEVICE_UPDATE_INTERVAL);
                    break;
            }

        }
    }

    public DeviceRun(Context context) {
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessageDelayed(FLAG_DEVICE_UPDATE, 1000 * 3);
        mSp = context.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(FLAG_DEVICE_UPDATE);
    }
}
