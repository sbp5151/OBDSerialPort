package com.jld.obdserialport.bean;

import android.util.Log;

import com.jld.obdserialport.utils.Constant;

/**
 * 驾驶习惯数据
 */
public class HBTBean extends BaseBean {

    private static final String TAG = "HBTBean";
    private String obdId = Constant.OBD_DEFAULT_ID;//OBD编号
    private int totalIgnitionTimes;//总点火次数
    private double totalTravelTime;//累计行驶时间
    private double totalIdleTime;//累计怠速时间
    private double avgHotCarTime;//平均热车时间
    private double avgCarSpeed;//平均车速
    private double hisTopTurnSpeed;//最高车速
    private double hisTopCarSpeed;//最高转速
    private int totalRapidlyAccelerateTimes;//累计急加速次数
    private int totalSharpSlowdownTimes;//累计急加速次数

    public void setData(String data) {
        Log.d(TAG, "HBTBean setData: " + data);
        String[] datas = data.split(",");
        if (datas.length == 10) {
            totalIgnitionTimes = Integer.parseInt(datas[1]);
            totalTravelTime = Double.parseDouble(datas[2]);
            totalIdleTime = Double.parseDouble(datas[3]);
            avgHotCarTime = Double.parseDouble(datas[4]);
            avgCarSpeed = Double.parseDouble(datas[5]);
            hisTopTurnSpeed = Double.parseDouble(datas[6]);
            hisTopCarSpeed = Double.parseDouble(datas[7]);
            totalRapidlyAccelerateTimes = Integer.parseInt(datas[8]);
            totalSharpSlowdownTimes = Integer.parseInt(datas[9]);
        } else {
            Log.e(TAG, "驾驶习惯数据 setData 数据大小异常:" + data);
        }
    }

    @Override
    public String toString() {
        return "HBTBean{" +
                "obdId='" + obdId + '\'' +
                ", totalIgnitionTimes=" + totalIgnitionTimes +
                ", totalTravelTime=" + totalTravelTime +
                ", totalIdleTime=" + totalIdleTime +
                ", avgHotCarTime=" + avgHotCarTime +
                ", avgCarSpeed=" + avgCarSpeed +
                ", hisTopTurnSpeed=" + hisTopTurnSpeed +
                ", hisTopCarSpeed=" + hisTopCarSpeed +
                ", totalRapidlyAccelerateTimes=" + totalRapidlyAccelerateTimes +
                ", totalSharpSlowdownTimes=" + totalSharpSlowdownTimes +
                '}';
    }
}
