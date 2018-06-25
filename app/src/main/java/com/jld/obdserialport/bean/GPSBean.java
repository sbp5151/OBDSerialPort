package com.jld.obdserialport.bean;

import android.content.Context;

import com.jld.obdserialport.utils.Constant;

public class GPSBean {

    private String obdId = Constant.OBD_DEFAULT_ID;
    private double direction;
    private double longitude;
    private double latitude;

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDirection() {
        return direction;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
