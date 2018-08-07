package com.jld.obdserialport.bean.request;

import android.util.Log;

/**
 * 车辆实时数据流
 */
public class RTBean extends RequestBaseBean {

    public static final String TAG = "RTBean";
    private String mBatteryVoltage;//1电瓶电压
    private String engineSpeed;//2发动机转速（动力）
    private String carSpeed;//3行驶车速
    private String throttleOpening;//4节气门开度
    private String engineLoad;//5发动机负荷
    private String coolantTemperature;//6冷却液温度
    private String momentOilWear;//7瞬时油耗
    private String averageOilWear;//8平均油耗
    private double thisMileage;//9本次行驶里程
    private double totalMileage;// 10总里程(km) AT300需解析
    private double thisFuelConsumption;//11本次耗油量(L)
    private double totalFuelConsumption;//12累计耗油量(L
    private String currentNumberOfFaultCodes;//13当前故障码数量
    private int rapidlyAccelerateTimes;//14本次急加速次数
    private int sharpSlowdownTimes;//15本次急减速次数

    public RTBean() {
        super();
    }

    public boolean setData(String data) {
        String[] datas = data.split(",");
        if (datas.length == 16) {
            mBatteryVoltage = datas[1];
            engineSpeed = datas[2];
            carSpeed = datas[3];
            throttleOpening = datas[4];
            engineLoad = datas[5];
            coolantTemperature = datas[6];
            momentOilWear = datas[7];
            averageOilWear = datas[8];
            thisMileage = Double.parseDouble(datas[9]);
            totalMileage = Double.parseDouble(datas[10]);
            thisFuelConsumption = Double.parseDouble(datas[11]);
            totalFuelConsumption = Double.parseDouble(datas[12]);
            currentNumberOfFaultCodes = datas[13];
            rapidlyAccelerateTimes = Integer.parseInt(datas[14]);
            sharpSlowdownTimes = Integer.parseInt(datas[15]);
            return true;
        } else {
            Log.e(TAG, "车辆实时数据流 setData 数据大小异常:" + data);
            return false;
        }
    }

    public String getEngineSpeed() {
        return engineSpeed;
    }

    public String getBatteryVoltage() {
        return mBatteryVoltage;
    }

    @Override
    public String toString() {
        return "RTBean{" +
                "mBatteryVoltage='" + mBatteryVoltage + '\'' +
                ", engineSpeed='" + engineSpeed + '\'' +
                ", carSpeed='" + carSpeed + '\'' +
                ", throttleOpening='" + throttleOpening + '\'' +
                ", engineLoad='" + engineLoad + '\'' +
                ", coolantTemperature='" + coolantTemperature + '\'' +
                ", momentOilWear='" + momentOilWear + '\'' +
                ", averageOilWear='" + averageOilWear + '\'' +
                ", thisMileage=" + thisMileage +
                ", totalMileage=" + totalMileage +
                ", thisFuelConsumption=" + thisFuelConsumption +
                ", totalFuelConsumption=" + totalFuelConsumption +
                ", currentNumberOfFaultCodes='" + currentNumberOfFaultCodes + '\'' +
                ", rapidlyAccelerateTimes=" + rapidlyAccelerateTimes +
                ", sharpSlowdownTimes=" + sharpSlowdownTimes +
                '}';
    }
}
