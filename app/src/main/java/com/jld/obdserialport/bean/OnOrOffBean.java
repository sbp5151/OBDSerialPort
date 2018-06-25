package com.jld.obdserialport.bean;

import com.jld.obdserialport.utils.Constant;

public class OnOrOffBean extends BaseBean {

    private String obdId = Constant.OBD_DEFAULT_ID;//OBD编号
    private double engineState;//发动机状态
    private double longitude;//经度
    private double latitude;//纬度

    public static final double CAR_START = 1;
    public static final double CAR_STOP = 0;

    public OnOrOffBean(double engineState, double longitude, double latitude) {
        this.engineState = engineState;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getEngineState() {
        return engineState;
    }

    public void setEngineState(double engineState) {
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
