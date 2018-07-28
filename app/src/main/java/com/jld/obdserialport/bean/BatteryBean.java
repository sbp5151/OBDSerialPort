package com.jld.obdserialport.bean;

import com.jld.obdserialport.utils.Constant;

public class BatteryBean {

    private double batteryVoltage;

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    @Override
    public String toString() {
        return "BatteryBean{" +
                ", batteryVoltage=" + batteryVoltage +
                '}';
    }
}
