package com.jld.obdserialport.bean.response;

public class CheckBindBean {

    private int code;
    private int isBinding;
    private String jPushAlias;
    private String userName;
    private String mobile;

    public CheckBindBean() {
        super();
    }

    public int getCode() {
        return code;
    }
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
