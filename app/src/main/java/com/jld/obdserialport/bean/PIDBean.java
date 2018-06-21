package com.jld.obdserialport.bean;

public class PIDBean {

    private String obdId;// OBD编号
    private double batteryVoltage;// 电瓶电压(V) AT297
    private double engineSpeed; //发动机转速(rpm/转) AT012
    private double carSpeed; // 行驶车速(Km/h) AT013
    private double throttleOpening;//节气门开度(%) AT017
    private double  engineLoad;//发动机负荷(%) AT004
    private double coolantTemperature;//冷却液温度(℃) AT005
    private double momentOilWear; //瞬时油耗（怠速）：L/h （行驶）：L/100km AT298
    private double averageOilWear;// 平均油耗(L/100km) AT299
    private double thisMileage;//本次行驶里程(km) AT300需解析
    private double totalMileage;// 总里程(km) AT300需解析
    private double thisFuelConsumption;//本次耗油量(L) AT301需解析
    private double totalFuelConsumption;//累计耗油量(L)AT301需解析
    private int currentNumberOfFaultCodes;//当前故障码数量(Times/次) AT400需解析
    private double  drivingTime;//驾驶时间信息(h) AT303
    private double engineRunTime;//引擎运行时间(h) AT031
    private double inletTemperature;//进气温度(℃) AT015
    private double fuelTankResidue;//油箱剩余油量(%) AT047
    private double gerInstructionOpening;//EGR指令开度(%) AT044
//    private String carState;//行车状态（怠速/行驶）这个状态详细讨论后决定（）
    private int rapidlyAccelerateTimes = 0;//本次急加速次数(Times/次)
    private int sharpSlowdownTimes = 0;//本次急减速次数(Times/次)

}
