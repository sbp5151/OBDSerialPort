package com.jld.obdserialport.bean;

import com.jld.obdserialport.utils.Constant;

public class DeviceBean {

    private String obdId = Constant.OBD_DEFAULT_ID;//OBD编号
    private String deviceAlias;
    private boolean isBind;
    private String bindName;
}
