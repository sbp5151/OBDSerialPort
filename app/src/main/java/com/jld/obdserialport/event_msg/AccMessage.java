package com.jld.obdserialport.event_msg;

public class AccMessage {

    public static final int EVENT_FLAG_ACC_ON = 1;
    public static final int EVENT_FLAG_ACC_OFF = 2;
    private int mEventFlag;

    public AccMessage(int eventFlag) {
        mEventFlag = eventFlag;
    }

    public int getEventFlag() {
        return mEventFlag;
    }

    public void setEventFlag(int eventFlag) {
        mEventFlag = eventFlag;
    }
}
