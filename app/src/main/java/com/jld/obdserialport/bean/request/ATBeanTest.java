package com.jld.obdserialport.bean.request;

import android.util.Log;

/**
 * 单独数据读取
 */
public class ATBeanTest extends RequestBaseBean {
    private static final String TAG = "ATBeanTest";
    private int currentNumberOfFaultCodes;//当前故障码数量(Times/次) 01
    private double engineLoad;//发动机负荷(%) 004
    private double coolantTemperature;//冷却液温度(℃) 005
    private double engineSpeed; //发动机转速(rpm/转) 012
    private double carSpeed; // 行驶车速(Km/h) 013
    private double inletTemperature;//进气温度(℃) 015
    private double throttleOpening;//节气门开度(%) 017
    private double engineRunTime;//引擎运行时间(h) 031
    private double gerInstructionOpening;//EGR指令开度(%) 044
    private double fuelTankResidue;//油箱剩余油量(%) 047
    private double momentOilWear = 10; //瞬时油耗（怠速）：L/h （行驶）：L/100km AT298
    private double averageOilWear = 10;// 平均油耗(L/100km) AT299
    private double thisMileage = 100;//本次行驶里程(km) AT300需解析
    private double totalMileage = 10000;// 总里程(km) AT300需解析
    private double thisFuelConsumption = 200;//本次耗油量(L) AT301需解析
    private double totalFuelConsumption = 20000;//累计耗油量(L)AT301需解析
    private double drivingTime = 3;//驾驶时间信息(h) AT303
    private double batteryVoltage = 100;// 电瓶电压(V) AT297
    private int rapidlyAccelerateTimes = 0;//本次急加速次数(Times/次)
    private int sharpSlowdownTimes = 0;//本次急减速次数(Times/次)

    public ATBeanTest() {
        super();
    }

    public boolean setData(String data) {
        Log.d(TAG, "setData: " + data);
        String[] split = data.split("=");
        Log.d(TAG, "split.length " + split.length);
        if (split.length != 2)
            return false;
        String pidTag = split[0].trim();
        String pidData = split[1].trim();
        if (pidTag.equals("PID1")) {
            Log.d(TAG, "SET PID1");
            currentNumberOfFaultCodes = Integer.parseInt(pidData);
        } else if (pidTag.equals("PID4")) {
            engineLoad = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID5")) {
            coolantTemperature = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID12")) {
            engineSpeed = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID13")) {
            carSpeed = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID15")) {
            inletTemperature = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID17")) {
            throttleOpening = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID31")) {
            engineRunTime = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID44")) {
            gerInstructionOpening = Double.parseDouble(pidData);
        } else if (pidTag.equals("PID47")) {
            Log.d(TAG, "SET PID47");
            fuelTankResidue = Double.parseDouble(pidData);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ATBeanTest{" +
                ", currentNumberOfFaultCodes=" + currentNumberOfFaultCodes +
                ", engineLoad=" + engineLoad +
                ", coolantTemperature=" + coolantTemperature +
                ", engineSpeed=" + engineSpeed +
                ", carSpeed=" + carSpeed +
                ", inletTemperature=" + inletTemperature +
                ", throttleOpening=" + throttleOpening +
                ", engineRunTime=" + engineRunTime +
                ", gerInstructionOpening=" + gerInstructionOpening +
                ", fuelTankResidue=" + fuelTankResidue +
                ", momentOilWear=" + momentOilWear +
                ", averageOilWear=" + averageOilWear +
                ", thisMileage=" + thisMileage +
                ", totalMileage=" + totalMileage +
                ", thisFuelConsumption=" + thisFuelConsumption +
                ", totalFuelConsumption=" + totalFuelConsumption +
                ", drivingTime=" + drivingTime +
                ", batteryVoltage=" + batteryVoltage +
                ", rapidlyAccelerateTimes=" + rapidlyAccelerateTimes +
                ", sharpSlowdownTimes=" + sharpSlowdownTimes +
                '}';
    }
}
