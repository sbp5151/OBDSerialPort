package com.jld.obdserialport.bean;

import com.jld.obdserialport.utils.Constant;

public class StartOrStopBean {

    private String obdId = Constant.OBD_DEFAULT_ID;//OBD编号
    private String engineState;//发动机状态 点火/熄火
    private double longitude;//经度
    private double latitude;//纬度

    public static final String CAR_START = "点火";
    public static final String CAR_STOP = "熄火";

    public StartOrStopBean(String engineState, double longitude, double latitude) {
        this.engineState = engineState;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getEngineState() {
        return engineState;
    }

    public void setEngineState(String engineState) {
        this.engineState = engineState;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
