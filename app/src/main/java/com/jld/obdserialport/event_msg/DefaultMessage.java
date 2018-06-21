package com.jld.obdserialport.event_msg;

public class DefaultMessage {

    //显示二维码
    public static final int EVENT_MSG_HIDE_CODE = 0x07;
    //隐藏二维码
    public static final int EVENT_MSG_SHOW_CODE = 0x08;
    //网络错误
    public static final int EVENT_MSG_NETWORK_ERROR = 0x09;

    private int flag;
    private String message;

    public DefaultMessage(int flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public DefaultMessage() {
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
