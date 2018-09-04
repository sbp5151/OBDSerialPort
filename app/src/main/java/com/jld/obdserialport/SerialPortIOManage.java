package com.jld.obdserialport;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.runnable.LogRecordRun;
import com.jld.obdserialport.utils.TestLogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 串口数据读写管理类
 * 功能：连接、读写
 */
public class SerialPortIOManage {
    public static final String TAG = "SerialPortIOManage";
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private boolean mIsConnect = false;
    private HandlerThread mHandlerThread;
    private SerialPortDevice mSpd;
    private Context mContext;
    private final EventBus mEventBus;
    private boolean mIsLoopWrite = true;
    private boolean mIsLoopRead = true;
    private boolean mIsWriteDataIng = false;
    private final int WRITE_DATA_FLAG = 0x01;
    private final int FEEDBACK_TIMEOUT_FLAG = 0x02;
    private final int mFeedbackTimeout = 1500;//发送数据反馈超时
    private ArrayList<String> mWriteDatas = new ArrayList<>();
    private WriteHandler mWriteHandler;

    public SerialPortIOManage(Context context) {
        mContext = context;
        mEventBus = EventBus.getDefault();
    }

    /**
     * OBD连接
     *
     * @param path
     * @param baudRate
     */
    public void connect(String path, int baudRate) {
        if (mIsConnect)
            return;
        mSpd = new SerialPortDevice(path, baudRate);
        if (mSpd.connect()) {
            mIsConnect = true;
            mInputStream = mSpd.getInputStream();
            mOutputStream = mSpd.getOutputStream();
            new Thread(mReadRun).start();//启动串口数据接收
            new Thread(mLoopWriteRun).start();//循环写数据
            mHandlerThread = new HandlerThread("");//数据发送线程
            mHandlerThread.start();
            mWriteHandler = new WriteHandler(mHandlerThread.getLooper(), this);
            Log.d(TAG, "串口连接成功");
            TestLogUtil.log( "串口连接成功");
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONNECT_STATE_FLAG, true));
        } else {
            LogRecordRun.getInstance().writeLog("串口连接失败");
            Log.d(TAG, "串口连接失败");
            TestLogUtil.log( "串口连接失败");
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONNECT_STATE_FLAG, false));
            mIsConnect = false;
        }
    }

    class WriteHandler extends Handler {
        WeakReference<SerialPortIOManage> mWeakReference;
        private WriteHandler(Looper looper, SerialPortIOManage manage) {
            super(looper);
            mWeakReference = new WeakReference<>(manage);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case WRITE_DATA_FLAG:
                    String data = msg.obj + "\r\n";
                    try {
                        Log.d(TAG, "写数据：" + data);
                        mOutputStream.write(data.getBytes("UTF-8"));
                        mOutputStream.flush();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "写入失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                case FEEDBACK_TIMEOUT_FLAG:
                    mIsWriteDataIng = false;
                    break;
            }
        }
    }

    public void addWriteData(String data) {
        if (mIsConnect) {
            mWriteDatas.add(data);
        }
    }

    /**
     * 向串口写数据
     */
    private Runnable mLoopWriteRun = new Runnable() {
        @Override
        public void run() {
            while (mIsLoopWrite) {
                if (!mIsWriteDataIng && mWriteDatas.size() > 0) {
                    mIsWriteDataIng = true;
                    Message message = mWriteHandler.obtainMessage();
                    message.obj = mWriteDatas.get(0);
                    message.what = WRITE_DATA_FLAG;
                    mWriteHandler.sendMessage(message);
                    mWriteHandler.sendEmptyMessageDelayed(FEEDBACK_TIMEOUT_FLAG, mFeedbackTimeout);//数据反馈超时
                    mWriteDatas.remove(0);
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private byte[] mBuffer = new byte[512];
    /**
     * 串口数据接收
     */
    private Runnable mReadRun = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "开始读取数据:" + mIsConnect);
            while (mIsLoopRead) {
                try {
                    mIsWriteDataIng = false;
                    int read = mInputStream.read(mBuffer);
                    String readData = new String(mBuffer, 0, read, "UTF-8");
                    Log.d(TAG, "数据读取：" + readData);
                    LogRecordRun.getInstance().writeLog(readData);
                    mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, readData));
                    mWriteHandler.removeMessages(FEEDBACK_TIMEOUT_FLAG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 断开连接
     */
    public void disConnect() {
        LogRecordRun.getInstance().onDestroy();
        mIsLoopRead = false;
        mIsLoopWrite = false;
        if (!mIsConnect)
            return;
        mSpd.disConnect();
        mIsConnect = false;
        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    public boolean isConnect() {
        return mIsConnect;
    }
}