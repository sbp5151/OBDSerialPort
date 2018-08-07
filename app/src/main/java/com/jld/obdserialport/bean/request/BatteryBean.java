package com.jld.obdserialport.bean.request;

import com.jld.obdserialport.MyApplication;

public class BatteryBean extends RequestBaseBean {

    private double batteryVoltage;

    public BatteryBean() {
        super();
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
        obdId = MyApplication.OBD_ID;
    }

    @Override
    public String toString() {
        return "BatteryBean{" +
                ", batteryVoltage=" + batteryVoltage +
                '}';
    }
}
