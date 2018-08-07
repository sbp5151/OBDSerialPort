package com.jld.obdserialport.bean.request;

import com.jld.obdserialport.MyApplication;

public class RequestBaseBean {
    protected String obdId = MyApplication.OBD_ID;//OBD编号
    public RequestBaseBean() {
//        this.obdId = MyApplication.OBD_ID;
    }
    public String getObdId() {
        return obdId;
    }

    public void setObdId(String obdId) {
        this.obdId = obdId;
    }
}
