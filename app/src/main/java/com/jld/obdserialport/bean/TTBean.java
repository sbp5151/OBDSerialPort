package com.jld.obdserialport.bean;

import android.util.Log;

import com.jld.obdserialport.utils.Constant;

/**
 * 本次行程统计数据流
 * 每次熄火发送一次
 */
public class TTBean {

    private static final String TAG = "TTBean";
    private String obdId = Constant.OBD_DEFAULT_ID;//OBD编号
    private double hotCarTimeLong;//本次热车时长
    private double idleSpeedTimeLong;//本次怠速时长
    private double drivingTimeLong;//本次行驶时长
    private double mileage = 10;//本次行驶里程
    private double idleSpeedFuelConsumption;//本次怠速耗油
    private double drivingFuelConsumption;//本次行驶耗油
    private double topTurnSpeed;//本次最高转速
    private double topCarSpeed;//本次最高车速
    private int rapidlyAccelerateTimes;//本次急加速次数
    private int sharpSlowdownTimes;//本次急减速次数

    public void setData(String data) {
        String[] datas = data.split(",");
        if (datas.length >= 11) {
            hotCarTimeLong = Double.parseDouble(datas[1]);
            idleSpeedTimeLong = Double.parseDouble(datas[2]);
            drivingTimeLong = Double.parseDouble(datas[3]);
            mileage = Double.parseDouble(datas[4]);
            idleSpeedFuelConsumption = Double.parseDouble(datas[5]);
            drivingFuelConsumption = Double.parseDouble(datas[6]);
            topTurnSpeed = Double.parseDouble(datas[7]);
            topCarSpeed = Double.parseDouble(datas[8]);
            rapidlyAccelerateTimes = Integer.parseInt(datas[9]);
            sharpSlowdownTimes = Integer.parseInt(datas[10]);
        } else {
            Log.e(TAG, "TTBean setData 数据异常:" + data);
        }
    }
    public double getTravelMileage() {
        return mileage;
    }
}
