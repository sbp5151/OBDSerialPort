package com.jld.obdserialport.bean;

public class DeviceBindMsg {

    private int result;
    private String msg;
    private String isBinding;
    private String jPushAlias;
    private String userName;
    private String mobile;

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public String getIsBinding() {
        return isBinding;
    }

    public String getjPushAlias() {
        return jPushAlias;
    }

    public String getUserName() {
        return userName;
    }

    public String getMobile() {
        return mobile;
    }
}
