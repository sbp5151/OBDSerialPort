package com.jld.obdserialport.bean.response;

public class JpushLocationBean extends JpushBase {

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

    @Override
    public String toString() {
        return "JpushLocationBean{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", nickName='" + nickName + '\'' +
                ", openId='" + openId + '\'' +
                ", obdId='" + obdId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", address='" + address + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}
