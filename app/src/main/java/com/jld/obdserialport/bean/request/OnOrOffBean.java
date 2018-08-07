package com.jld.obdserialport.bean.request;

public class OnOrOffBean extends RequestBaseBean {

    private String engineState;//发动机状态
    private double longitude;//经度
    private double latitude;//纬度
    private double fuelTankResidue;//剩余油量
    public static final String CAR_START = "点火";
    public static final String CAR_STOP = "熄火";

    public OnOrOffBean() {
        super();
    }

    public OnOrOffBean(String engineState, double longitude, double latitude, double fuelTankResidue) {
        this.engineState = engineState;
        this.longitude = longitude;
        this.latitude = latitude;
        this.fuelTankResidue = fuelTankResidue;
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
