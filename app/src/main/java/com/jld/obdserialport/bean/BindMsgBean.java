package com.jld.obdserialport.bean;

public class BindMsgBean extends BaseBean{

    private int isBinding;
    private String jPushAlias;
    private String userName;
    private String mobile;

    public int getIsBinding() {
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
