package com.jld.obdserialport.utils;

public class Constant {

    public static final String SIGN_KEY = "abc_futureiv_123";

    /**
     * GPS数据上传接口
     */
    public static final String URL_GPS_POST = "http://m.futurevi.com/androidFaceController/uploadLocationInfoFace.do";
    /**
     * 实时数据上传
     */
    public static final String URL_RT_POST = "http://m.futurevi.com/androidFaceController/uploadCarNowDataFace.do";

    /**
     * 上传PID数据接口
     */
    public static final String URL_PID_POST = "http://m.futurevi.com/androidFaceController/uploadCarNowDataFace.do";
    /**
     * 上传驾驶习惯数据接口
     */
    public static final String URL_HBT_POST = "http://m.futurevi.com/androidFaceController/uploadDrivingBehaviorFace.do";
    /**
     * 上传本次行程数据接口
     */
    public static final String URL_TT_POST = "http://m.futurevi.com/androidFaceController/uploadSingleStrokeStatisticsFace.do";
    /**
     * 汽车启停接口
     */
    public static final String URL_CAR_ONOFF_POST = "http://m.futurevi.com/androidFaceController/uploadCarStateInfoFace.do";

    /**
     * 获取JPush绑定信息
     */
    public static final String URL_REQUEST_BIND_MSG = "http://m.futurevi.com/androidFaceController/getObdInfo.do";

    /**
     * 上传JPush绑定信息
     */
    public static final String URL_UPLOAD_BIND_MSG = "http://m.futurevi.com/androidFaceController/uploadJGTokenByObdId.do";

    /**
     * 上传设备ID
     */
    public static final String URL_UPLOAD_DEVICE_ID = "http://m.futurevi.com/androidFaceController/uploadObdId.do";

    /**
     * 电池电压上传接口
     */
    public static final String URL_UPLOAD_BATTERY_VOLTAGE = "http://m.futurevi.com/androidFaceController/uploadBatteryVoltageFace.do";

    /**
     * 检测APK是否能升级接口
     */
    public static final String URL_CHECK_APK_UPDATE = "http://m.futurevi.com/androidFaceController/isUpdateApkVersion.do";

    /**
     * 相片上传
     */
    public static final String URL_MEDIA_UPLOAD = "http://m.futurevi.com/androidFaceController/uploadObdFile.do";

    /**
     * 设备在线状态更新
     */
    public static final String URL_DEVICE_ONLINE = "http://m.futurevi.com/androidFaceController/uploadObdOnlineStatus.do";

    /**
     * OBD默认ID
     */
//    public static final String OBD_ID = "Future_V1.0_00006";
//    public static final String OBD_ID = "Future_V1.0_00006";
//    public static final String OBD_ID = "Future_V1.0_00006";
//    public static final String OBD_ID = "Future_V1.0_00006";//周总
//    public static final String OBD_ID = "Future_V1.0_08888";//邹总


    //串口访问路径
//    public static String SERIAL_PORT_PATH = "/dev/ttyS2";
    public static String SERIAL_PORT_PATH = "/dev/ttyMT2";
    //串口波特率
    public static int SERIAL_PORT_BAUD_RATE = 9600;
    //shared名称
    public static final String SHARED_NAME = "shared_futurevi";
}
