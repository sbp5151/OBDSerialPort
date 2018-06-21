package com.jld.obdserialport.utils;

public class Constant {

    public static final String SIGN_KEY = "abc_futureiv_123";
    /**
     * 上传PID数据接口
     */
    public static final String URL_PID_POST = "http://192.168.3.206:8080/CarFuture/androidFaceController/uploadCarNowDataFace.do";
    /**
     * 上传驾驶习惯数据接口
     */
    public static final String URL_HBT_POST = "http://192.168.3.206:8080/CarFuture/androidFaceController/uploadDrivingBehaviorFace.do";
    /**
     * 上传本次行程数据接口
     */
    public static final String URL_TT_POST = "http://192.168.3.206:8080/CarFuture/androidFaceController/uploadSingleStrokeStatisticsFace.do";
    /**
     * 汽车启停接口
     */
    public static final String URL_CAR_ONOFF_POST = "http://192.168.3.206:8080/CarFuture/androidFaceController/uploadCarStateInfoFace.do";

    /**
     * 获取JPush绑定信息
     */
    public static final String URL_REQUEST_BIND_MSG = "http://192.168.3.113:8080/androidFaceController/getObdInfo.do";

    /**
     * 上传JPush绑定信息
     */
    public static final String URL_UPLOAD_BIND_MSG = "http://192.168.3.113:8080/androidFaceController/uploadJGTokenByObdId.do";

    /**
     * 上传设备ID
     */
    public static final String URL_UPLOAD_DEVICE_ID = "http://192.168.3.113:8080/androidFaceController/uploadObdId.do";

    /**
     * OBD默认ID
     */
    public static final String OBD_DEFAULT_ID = "JLD001";

    /**
     * 极光推送别名
     */
    public static final String JPUSH_DEVICE_ALIAS = OBD_DEFAULT_ID;

    //串口访问路径
//    public static String SERIAL_PORT_PATH = "/dev/ttyS2";
    public static String SERIAL_PORT_PATH = "/dev/ttyMT2";
    //串口波特率
    public static int SERIAL_PORT_BAUD_RATE = 57600;
//    public static int SERIAL_PORT_BAUD_RATE = 9600;

    //shared名称
    public static final String SHARED_NAME = "shared_futurevi";
}
