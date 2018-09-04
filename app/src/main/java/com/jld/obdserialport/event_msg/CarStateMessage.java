package com.jld.obdserialport.event_msg;

public class CarStateMessage {

    public static final int CAR_FLAG_FLAG_START = 1;
    public static final int CAR_FLAG_FLAG_STOP = 2;
    private int mFlag;

    public CarStateMessage(int flag) {
        mFlag = flag;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }
}
