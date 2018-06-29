package com.jld.obdserialport.bean;

import com.jld.obdserialport.utils.Constant;

public class BatteryBean {

    private String obdId = Constant.OBD_DEFAULT_ID;
    private double batteryVoltage;

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    @Override
    public String toString() {
        return "BatteryBean{" +
                "obdId='" + obdId + '\'' +
                ", batteryVoltage=" + batteryVoltage +
                '}';
    }
}
