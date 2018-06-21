package com.jld.obdserialport.event_msg;

public class OBDDataMessage {

    public static final int CONNECT_STATE_FLAG = 1;//连接状态标记
    public static final int CONTENT_FLAG = 2;//OBD内容标记
    private int mFlag;
    private String mMessage;
    private boolean mIsConnect;

    public OBDDataMessage(int flag, boolean isConnect) {
        mFlag = flag;
        mIsConnect = isConnect;
    }

    public OBDDataMessage(int flag, String message) {
        mFlag = flag;
        mMessage = message;
    }

    public boolean isConnect() {
        return mIsConnect;
    }

    public void setConnect(boolean connect) {
        mIsConnect = connect;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    @Override
    public String toString() {
        return "OBDDataMessage{" +
                "mFlag=" + mFlag +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }
}
