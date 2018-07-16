package com.jld.obdserialport.event_msg;

public class MediaMessage {

    private int flag;
    public static final int EVENT_MSG_PHOTO = 0x01;
    public static final int EVENT_MSG_VIDEO = 0x02;
    public MediaMessage(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
