package com.jld.obdserialport.runnable;

import android.os.Environment;
import android.util.Log;

import com.jld.obdserialport.event_msg.AccMessage;
import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LogRecordRun extends BaseRun {

    private static final String TAG = "LogRecordRun";
    private  FileOutputStream mFos;
    private  OutputStreamWriter mOsw;
    private static LogRecordRun logRecordRun;

    public static LogRecordRun getInstance() {
        if (logRecordRun == null)
            logRecordRun = new LogRecordRun();
        return logRecordRun;
    }

    private LogRecordRun() {
//        Log.e(TAG, "LogRecordRun:");
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Future" + File.separator + TimeUtils.getNowMills() + "log.txt";
//        File mFile = new File(path);
//        if (!mFile.getParentFile().exists() && !mFile.getParentFile().mkdir())
//            return;
//        try {
//            if (!mFile.exists() && !mFile.createNewFile())
//                return;
//            mFos = new FileOutputStream(mFile);
//            mOsw = new OutputStreamWriter(mFos, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void odbEvent(AccMessage accMessage) {
        if (accMessage.getEventFlag() == AccMessage.EVENT_FLAG_ACC_OFF) {
            writeLog("ACC_OFF");
        } else if (accMessage.getEventFlag() == AccMessage.EVENT_FLAG_ACC_ON)
            writeLog("ACC_ON");
    }

    public void writeLog(String logStr) {
//        Log.e(TAG, "writeLog:" + logStr);
//        if (mOsw != null) {
//            try {
//                mOsw.write(logStr + TimeUtils.getNowString() + "\r\n");
//                mOsw.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "创建文件夹失败");
//            }
//        } else Log.e(TAG, "mOsw为null");
    }

    @Override
    public void onDestroy() {
//        try {
//            mOsw.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mOsw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mFos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
