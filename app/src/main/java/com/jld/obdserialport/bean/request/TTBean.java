package com.jld.obdserialport.bean.request;

import android.util.Log;

import com.jld.obdserialport.utils.TestLogUtil;

/**
 * 本次行程统计数据流
 * 每次熄火发送一次
 */
public class TTBean extends RequestBaseBean {

    private static final String TAG = "TTBean";
    private double hotCarTimeLong;//本次热车时长
    private double idleSpeedTimeLong;//本次怠速时长
    private double drivingTimeLong;//本次行驶时长
    private double mileage;//本次行驶里程
    private double idleSpeedFuelConsumption;//本次怠速耗油
    private double drivingFuelConsumption;//本次行驶耗油
    private double topTurnSpeed;//本次最高转速
    private double topCarSpeed;//本次最高车速
    private int rapidlyAccelerateTimes;//本次急加速次数
    private int sharpSlowdownTimes;//本次急减速次数
    private String startTime;//本次行程开始时间
    private String endTime;//本次行程结束时间

    public TTBean() {
        super();
    }

    public void setData(String data) {
        String[] datas = data.split(",");
        int ttIndex = -1;
        for (int i = 0; i < datas.length; i++) {
            Log.d(TAG, "setData: " + datas[i]);
            TestLogUtil.log("setData: " + datas[i]);
            if (datas[i].contains("TT"))
                ttIndex = i;
        }
        Log.d(TAG, "ttIndex: " + ttIndex);
        TestLogUtil.log("ttIndex：" + ttIndex);
        if (ttIndex >= 0 && datas.length > ttIndex + 10) {
            hotCarTimeLong = Double.parseDouble(datas[ttIndex + 1]);
            idleSpeedTimeLong = Double.parseDouble(datas[ttIndex + 2]);
            drivingTimeLong = Double.parseDouble(datas[ttIndex + 3]);
            mileage = Double.parseDouble(datas[ttIndex + 4]);
            idleSpeedFuelConsumption = Double.parseDouble(datas[ttIndex + 5]);
            drivingFuelConsumption = Double.parseDouble(datas[ttIndex + 6]);
            topTurnSpeed = Double.parseDouble(datas[ttIndex + 7]);
            topCarSpeed = Double.parseDouble(datas[ttIndex + 8]);
            rapidlyAccelerateTimes = Integer.parseInt(datas[ttIndex + 9]);
            sharpSlowdownTimes = Integer.parseInt(datas[ttIndex + 10]);
        } else
            TestLogUtil.log("TT数据异常：" + data);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getTravelMileage() {
        return mileage;
    }

    @Override
    public String toString() {
        return "TTBean{" +
                "hotCarTimeLong=" + hotCarTimeLong +
                ", idleSpeedTimeLong=" + idleSpeedTimeLong +
                ", drivingTimeLong=" + drivingTimeLong +
                ", mileage=" + mileage +
                ", idleSpeedFuelConsumption=" + idleSpeedFuelConsumption +
                ", drivingFuelConsumption=" + drivingFuelConsumption +
                ", topTurnSpeed=" + topTurnSpeed +
                ", topCarSpeed=" + topCarSpeed +
                ", rapidlyAccelerateTimes=" + rapidlyAccelerateTimes +
                ", sharpSlowdownTimes=" + sharpSlowdownTimes +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
