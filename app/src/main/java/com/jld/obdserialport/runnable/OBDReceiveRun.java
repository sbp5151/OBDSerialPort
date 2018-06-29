package com.jld.obdserialport.runnable;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jld.obdserialport.SerialPortIOManage;
import com.jld.obdserialport.bean.BatteryBean;
import com.jld.obdserialport.bean.HBTBean;
import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.bean.ATBeanTest;
import com.jld.obdserialport.bean.RTBean;
import com.jld.obdserialport.bean.OnOrOffBean;
import com.jld.obdserialport.bean.TTBean;
import com.jld.obdserialport.http.BaseHttpUtil;
import com.jld.obdserialport.http.OBDHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Date;

import static com.jld.obdserialport.utils.Constant.SERIAL_PORT_BAUD_RATE;
import static com.jld.obdserialport.utils.Constant.SERIAL_PORT_PATH;

/**
 * OBD运行任务
 * 功能：串口数据读取、串口数据控制（获取、上传）
 */
public class OBDReceiveRun {

    public static final String TAG = "OBDReceiveRun";
    private Context mContext;
    private final MyHandler mHandler;
    //串口连接标识
    private final int FLAG_PORT_CONNECT = 0x01;
    private final int FLAG_LOOP_GET_PID = 0x02;
    private final int FLAG_VOLTAGE_OFF_POST = 0x03;
    private final int FLAG_RT_POST = 0x04;
    private final int FLAG_ON_POST = 0x05;
    private final int FLAG_OFF_POST = 0x06;
    private final int FLAG_HBT_OFF_POST = 0x07;
    private final int FLAG_TT_POST = 0x08;
    private final int FLAG_REMAINING = 0x09;
    private final int FLAG_BATTERY_UPLOAD = 0x10;
    private final int FLAG_ENABLE_RT = 0x11;
    private final int ENGINE_STATUS_START = 1;
    private final int ENGINE_STATUS_STOP = 0;
    private SerialPortIOManage mPortManage;
    private RTBean mRtBean;
    private ATBeanTest mAtBean;
    private Date mTTStartTime;
    private OnOrOffBean mStartOrStopBean;
    private TTBean mTtBean;
    private HBTBean mHbtBean;
    //    //系统启动需要时间，可能接收不到汽车启动指令
//    //所以第一次启动时接收到RT数据则表示汽车启动
//    private boolean mIsFirstStart = true;
    private int mEngineStatus = ENGINE_STATUS_STOP;
    private final EventBus mEventBus;
    private BatteryBean mBatteryBean;

    private class MyHandler extends Handler {
        private WeakReference<OBDReceiveRun> mWeakReference;

        public MyHandler(OBDReceiveRun obdReceive) {
            mWeakReference = new WeakReference<>(obdReceive);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case FLAG_PORT_CONNECT://串口连接
                    Log.d(TAG, "connect: 串口连接：" + SERIAL_PORT_PATH + " -- " + SERIAL_PORT_BAUD_RATE);
                    mPortManage.connect(SERIAL_PORT_PATH, SERIAL_PORT_BAUD_RATE);
                    break;
                case FLAG_RT_POST:
                    OBDHttpUtil.build().rtDataPost(mRtBean, FLAG_RT_POST, mMyCallback);
                    break;
                case FLAG_HBT_OFF_POST:
                    OBDHttpUtil.build().hbtDataPost(mHbtBean, FLAG_HBT_OFF_POST, mMyCallback);
                    break;
                case FLAG_TT_POST:
                    OBDHttpUtil.build().ttDataPost(mTtBean, FLAG_TT_POST, mMyCallback);
                    break;
                case FLAG_ON_POST:
                    mStartOrStopBean = new OnOrOffBean(OnOrOffBean.CAR_START, LocationReceiveRun.mGpsBean.getLongitude(), LocationReceiveRun.mGpsBean.getLatitude(), mRemain);
                    OBDHttpUtil.build().carStartOrStopPost(mStartOrStopBean, FLAG_ON_POST, mMyCallback);
                    break;
                case FLAG_OFF_POST:
                    mStartOrStopBean = new OnOrOffBean(OnOrOffBean.CAR_STOP, LocationReceiveRun.mGpsBean.getLongitude(), LocationReceiveRun.mGpsBean.getLatitude(), mRemain);
                    OBDHttpUtil.build().carStartOrStopPost(mStartOrStopBean, FLAG_OFF_POST, mMyCallback);
                    break;
                case FLAG_REMAINING:
                    mPortManage.addWriteData("AT047");//获取当前剩余油量
                    mHandler.sendEmptyMessageDelayed(FLAG_REMAINING, 1000 * 60);
                    break;
                case FLAG_BATTERY_UPLOAD://电池电压上传
                    if (mRtBean == null)
                        return;
                    if (mBatteryBean == null)
                        mBatteryBean = new BatteryBean();
                    mBatteryBean.setBatteryVoltage(Double.parseDouble(mRtBean.getBatteryVoltage()));
                    OBDHttpUtil.build().BatteryVoltageDataPost(mBatteryBean);
                    mHandler.sendEmptyMessageDelayed(FLAG_BATTERY_UPLOAD, 1000 * 60 * 3);
                    break;
                case FLAG_ENABLE_RT:
//                    mHandler.sendEmptyMessageDelayed(FLAG_ENABLE_RT, 1000 * 60 * 10);
                    mHandler.sendEmptyMessageDelayed(FLAG_ENABLE_RT, 1000 * 60);
                    mPortManage.addWriteData("ATRON");//激活实时数据
                    break;
            }
        }
    }

    public OBDReceiveRun(Context context) {
        mContext = context;
        mHandler = new MyHandler(this);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "OBDReceiveRun run....."));
        mPortManage = new SerialPortIOManage(mContext);
        mHandler.sendEmptyMessage(FLAG_PORT_CONNECT);//串口连接
    }

    private int rtNum = 30;//实时数据30次才发送一次
    private int mRemain;//油箱剩余油量
    private String mLastEngineSpeed;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void odbEvent(OBDDataMessage messageEvent) {
        if (messageEvent.getFlag() == OBDDataMessage.CONNECT_STATE_FLAG) {//串口连接状态
            if (messageEvent.isConnect()) {
                Log.d(TAG, "串口连接成功");
            } else {
                Log.d(TAG, "串口连接失败 3s重连");
                mHandler.sendEmptyMessageDelayed(FLAG_PORT_CONNECT, 3000);
            }
        } else if (messageEvent.getFlag() == OBDDataMessage.CONTENT_FLAG) {//串口接收数据返回
            String message = messageEvent.getMessage();
            Log.d(TAG, "串口接收数据返回:" + message);
            if (message.startsWith("$OBD-RT")) {//实时数据
                rtParse(message);
            } else if (message.contains("System running")) {//汽车点火
            } else if (message.contains("System sleeping")) {//汽车熄火
//                mHandler.sendEmptyMessageDelayed(FLAG_ENABLE_RT, 1000 * 60 * 10);
                mHandler.sendEmptyMessageDelayed(FLAG_ENABLE_RT, 1000 * 60);
            } else if (message.contains("Connect ECU OK")) {
                mHandler.sendEmptyMessage(FLAG_REMAINING);//获取当前剩余油量
                mHandler.sendEmptyMessageDelayed(FLAG_BATTERY_UPLOAD, 1000 * 30);
                mPortManage.addWriteData("ATHBT");//获取驾驶习惯数据
                mPortManage.addWriteData("ATRON");//开启实时数据获取
            } else if (message.contains("BD-TT")) {//本次行程数据
                Log.d(TAG, "接收到本次行程数据");
                mTtBean = new TTBean();
                mTtBean.setData(message.trim());
                //本次行程大于等于500m
                //  if (mTtBean.getTravelMileage() >= 0.5) {
                mTtBean.setStartTime(mTTStartTime);
                mTtBean.setEndTime(new Date());
                mHandler.sendEmptyMessage(FLAG_TT_POST);
                //}
            } else if (message.startsWith("$OBD-HBT")) {//驾驶习惯数据
                Log.d(TAG, "接收到驾驶习惯数据");
                mHbtBean = new HBTBean();
                mHbtBean.setData(message.trim());
                mHandler.sendEmptyMessage(FLAG_HBT_OFF_POST);
            } else if (message.startsWith("$047=")) {//当前剩余油量
                Log.d(TAG, "接收到当前油耗：" + message);
                String remain = message.replace("$047=", "");
                if (!TextUtils.isEmpty(remain) && remain.equals("ECU not support")) {
                    mRemain = -1;
                } else if (!TextUtils.isEmpty(remain)) {
                    mRemain = Integer.parseInt(remain);
                }
            }
        }
    }

    private void rtParse(String message) {//实时数据解析
        if (mRtBean == null)
            mRtBean = new RTBean();
        mRtBean.setData(message.trim());
        String currentEngineSpeed = mRtBean.getEngineSpeed();
        //熄火判断
        if (!TextUtils.isEmpty(currentEngineSpeed) && !TextUtils.isEmpty(mLastEngineSpeed)
                && Integer.parseInt(currentEngineSpeed) < 300 && Integer.parseInt(mLastEngineSpeed) >= 300) {
            flameOut();//当上一次转速大于等于300，这次小于300代表熄火
        }
        mLastEngineSpeed = currentEngineSpeed;

        //点火判断
        if (!TextUtils.isEmpty(currentEngineSpeed) && Integer.parseInt(currentEngineSpeed) >= 300
                && mEngineStatus == ENGINE_STATUS_STOP) {
            ignition();//当当前转速大于等于300，发动机前面处于熄火状态，说明发动机在启动
        }

        //实时数据30次上传一次
        rtNum++;
        if (rtNum >= 30) {
            mHandler.sendEmptyMessage(FLAG_RT_POST);
            rtNum = 0;
        }
    }

    private void ignition() {
        Log.d(TAG, "判断汽车正在点火");
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "判断汽车正在点火"));

        mEngineStatus = ENGINE_STATUS_START;
        mTTStartTime = new Date();
        mHandler.sendEmptyMessage(FLAG_ON_POST);
    }

    private void flameOut() {
        Log.d(TAG, "判断汽车正在熄火");
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "判断汽车正在熄火"));
        mEngineStatus = ENGINE_STATUS_STOP;
        mHandler.sendEmptyMessage(FLAG_OFF_POST);
    }

    BaseHttpUtil.MyCallback mMyCallback = new BaseHttpUtil.MyCallback() {

        @Override
        public void onFailure(int tag, String errorMessage) {
//            mHandler.sendEmptyMessageDelayed(tag, 3000);
//            Log.d(TAG, "数据上传失败 tag: " + tag + " errorMessage:" + errorMessage);
        }

        @Override
        public void onResponse(int tag, String body) {
//            Log.d(TAG, "数据上传成功 tag: " + tag + " body:" + body);
        }
    };

    public void addWriteData(String data) {
        mPortManage.addWriteData(data);
    }

    public boolean isConnect() {
        return mPortManage.isConnect();
    }

    public void disConnect() {
        mPortManage.addWriteData("ATROFF");
        mHandler.removeMessages(FLAG_BATTERY_UPLOAD);
        mPortManage.disConnect();
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(FLAG_BATTERY_UPLOAD);
        mHandler.removeMessages(FLAG_PORT_CONNECT);
    }
}
