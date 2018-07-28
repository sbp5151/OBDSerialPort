package com.jld.obdserialport.bean;

import java.util.Date;

public class LocationInfoBean extends JpushBase{

    private String phoneNumber;
    private String nickName;//微信昵称
    private String openId;//微信用户唯一标识
    private String obdId;
    private double longitude;
    private double latitude;
    private String address;
    private String site;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public String getOpenId() {
        return openId;
    }

    public String getObdId() {
        return obdId;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public String getSite() {
        return site;
    }
}
