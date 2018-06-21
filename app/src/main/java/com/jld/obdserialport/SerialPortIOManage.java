package com.jld.obdserialport;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jld.obdserialport.event_msg.OBDDataMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SerialPortIOManage {
    public static final String TAG = "SerialPortIOManage";
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private boolean mIsConnect = false;
    private HandlerThread mHandlerThread;
    private Handler mWriteHandler;
    private SerialPortDevice mSpd;
    private Context mContext;
    private final EventBus mEventBus;
    private boolean mIsLoopWrite = true;
    private boolean mIsLoopRead = true;
    private boolean mIsWriteDataIng = false;
    private int mReadOff = 0;
    private final int WRITE_DATA_FLAG = 0x01;
    private final int FEEDBACK_TIMEOUT_FLAG = 0x02;
    private int mFeedbackTimeout = 1000;
    ArrayList<String> mWriteDatas = new ArrayList<>();

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
            new Thread(mLoopWriteRun).start();//循环写数据
            new Thread(mReadRun).start();//启动串口数据接收
            mHandlerThread = new HandlerThread("");//数据发送线程
            mHandlerThread.start();
            mWriteHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {//写数据
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case WRITE_DATA_FLAG:
                            String data = (String) msg.obj + "\r\n";
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
//                            sendData();
                            mIsWriteDataIng = false;
                            break;
                    }

                }
            };
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONNECT_STATE_FLAG, true));
        } else {
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONNECT_STATE_FLAG, false));
            mIsConnect = false;
        }
    }

    /**
     * 断开连接
     */
    public void disConnect() {
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

    public void addWriteData(String data) {
        if (mIsConnect) {
            mWriteDatas.add(data);
        }
    }

    /**
     * 向串口写数据
     */
    Runnable mLoopWriteRun = new Runnable() {
        @Override
        public void run() {
            while (mIsLoopWrite) {
//                if (!mIsWriteDataIng && mWriteDatas.size() > 0) {
                if (mWriteDatas.size() > 0) {
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
    byte[] mBuffer = new byte[512];

    /**
     * 串口数据接收
     */
    Runnable mReadRun = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mReadRun:" + mIsConnect);
            while (mIsLoopRead) {
                try {
//                    int read = mInputStream.read(mBuffer);
//                    mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, BinaryToHexString(mBuffer,read)));
//                    int read = mInputStream.read(mBuffer, mReadOff, mBuffer.length - mReadOff);
                    int read = mInputStream.read(mBuffer);
                    mReadOff = read;
                    Log.d(TAG, "read:"+read+"   数据读取: "+new String(mBuffer,0,read,"UTF-8"));
//                    mReadOff += read;
//                    Log.d(TAG, "数据读取: "+new String(mBuffer,0,mReadOff,"UTF-8"));
//                    if (read > 0 && mBuffer[mReadOff - 1] == 10) {//后缀为\n
//                        mWriteHandler.removeMessages(FEEDBACK_TIMEOUT_FLAG);
//                        sendData();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private static String hexStr =  "0123456789ABCDEF";
    /**
     *
     * @param bytes
     * @return 将二进制转换为十六进制字符输出
     */
    public static String BinaryToHexString(byte[] bytes,int length){
        String result = "";
        String hex = "";
        for(int i=0;i<length;i++){
            //字节高4位
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
            //字节低4位
            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
            result +=hex+" ";
        }
        return result;
    }
    private void sendData() {
        String readData = null;
        try {
            readData = new String(mBuffer, 0, mReadOff, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "readDataAll：" + readData);
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, readData));
        mReadOff = 0;
        mIsWriteDataIng = false;
    }
}