package com.jld.obdserialport.bean.request;

public class GPSBean extends RequestBaseBean {

    private double direction;
    private double longitude;
    private double latitude;
    private String address;

    public GPSBean() {
        super();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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
