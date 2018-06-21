package com.jld.obdserialport.bean;

/**
 * 车辆实时数据流
 */
public class RTBean {

    private String mBatteryVoltage;//电瓶电压
    private String mEngineSpeed;//发动机转速（动力）
    private String mTPPID;//节气门开度
    private String mEngineLoad;//发动机负荷
    private String mCoolantTemperature;//冷却液温度
    private String mInstantQtrip;//瞬时油耗
    private String mAverageQtrip;//平均油耗
    private String mQtripSurplus;//剩余油量
    private String mDTCNumber;//当前故障码数量
    private String mEngineStartTime;//发动机启动时间
    private String mIAT;//进气温度

    public void setData(String data){
        String[] datas = data.split(",");
        if(datas.length>=12){
            mBatteryVoltage = datas[0];
            mEngineSpeed = datas[1];
            mTPPID = datas[2];
            mEngineLoad = datas[3];
            mCoolantTemperature = datas[4];
            mInstantQtrip = datas[5];
            mAverageQtrip = datas[6];
            mQtripSurplus = datas[7];
            mDTCNumber = datas[8];
            mEngineStartTime = datas[9];
            mIAT = datas[10];
        }
    }
    @Override
    public String toString() {
        return "RTBean{" +
                "mBatteryVoltage='" + mBatteryVoltage + '\'' +
                ", mEngineSpeed='" + mEngineSpeed + '\'' +
                ", mTPPID='" + mTPPID + '\'' +
                ", mEngineLoad='" + mEngineLoad + '\'' +
                ", mCoolantTemperature='" + mCoolantTemperature + '\'' +
                ", mInstantQtrip='" + mInstantQtrip + '\'' +
                ", mAverageQtrip='" + mAverageQtrip + '\'' +
                ", mQtripSurplus='" + mQtripSurplus + '\'' +
                ", mDTCNumber='" + mDTCNumber + '\'' +
                ", mEngineStartTime='" + mEngineStartTime + '\'' +
                ", mIAT='" + mIAT + '\'' +
                '}';
    }
}
