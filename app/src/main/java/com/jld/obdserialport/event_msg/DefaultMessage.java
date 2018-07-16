package com.jld.obdserialport.event_msg;

public class DefaultMessage {

    //未绑定
    public static final int EVENT_MSG_SHOW_BIND_CODE = 0x07;
    //已绑定
    public static final int EVENT_MSG_BIND = 0x08;
    //网络错误
    public static final int EVENT_MSG_NETWORK_ERROR = 0x09;
    //帮定中
    public static final int EVENT_MSG_INIT_STATUS = 0x10;

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

    @Override
    public String toString() {
        return "DefaultMessage{" +
                "flag=" + flag +
                ", message='" + message + '\'' +
                '}';
    }
}
