package com.jld.obdserialport.runnable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jld.obdserialport.SerialPortIOManage;
import com.jld.obdserialport.bean.request.BatteryBean;
import com.jld.obdserialport.bean.request.HBTBean;
import com.jld.obdserialport.event_msg.AccMessage;
import com.jld.obdserialport.event_msg.CarStateMessage;
import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.bean.request.ATBeanTest;
import com.jld.obdserialport.bean.request.RTBean;
import com.jld.obdserialport.bean.request.OnOrOffBean;
import com.jld.obdserialport.bean.request.TTBean;
import com.jld.obdserialport.http.BaseHttpUtil;
import com.jld.obdserialport.http.OBDHttpUtil;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.SharedName;
import com.jld.obdserialport.utils.TestLogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jld.obdserialport.utils.Constant.SERIAL_PORT_BAUD_RATE;
import static com.jld.obdserialport.utils.Constant.SERIAL_PORT_PATH;

/**
 * OBD运行任务
 * 功能：串口数据读取、串口数据控制（获取、上传）
 */
public class OBDReceiveRun extends BaseRun {

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
    private final int FLAG_HBT_POST = 0x07;
    private final int FLAG_TT_POST = 0x08;
    private final int FLAG_REMAINING = 0x09;
    private final int FLAG_BATTERY_UPLOAD = 0x10;
    private final int FLAG_ENABLE_RT = 0x11;
    private final int FLAG_TT_START_TIME = 0x12;
    private final int ENGINE_STATUS_START = 1;
    private final int ENGINE_STATUS_STOP = 0;
    private SerialPortIOManage mPortManage;
    private RTBean mRtBean;
    private ATBeanTest mAtBean;
    private String mTTStartTime;
    private OnOrOffBean mStartOrStopBean;
    private TTBean mTtBean;
    private HBTBean mHbtBean;
    private int mEngineStatus = ENGINE_STATUS_STOP;
    private final EventBus mEventBus;
    private BatteryBean mBatteryBean;
    private final SimpleDateFormat mSimpleDateFormat;
    private final SharedPreferences mSp;

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
                    TestLogUtil.log("connect: 串口连接：" + SERIAL_PORT_PATH + " -- " + SERIAL_PORT_BAUD_RATE);
                    mPortManage.connect(SERIAL_PORT_PATH, SERIAL_PORT_BAUD_RATE);
                    break;
                case FLAG_RT_POST:
                    OBDHttpUtil.build().rtDataPost(mRtBean, FLAG_RT_POST);
                    break;
                case FLAG_HBT_POST:
                    OBDHttpUtil.build().hbtDataPost(mHbtBean, FLAG_HBT_POST, mMyCallback);
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
//                    mPortManage.addWriteData("AT047");
//                    mHandler.sendEmptyMessageDelayed(FLAG_REMAINING, 1000 * 60);
                    break;
                case FLAG_BATTERY_UPLOAD://电池电压上传 ECU连接成功开始上传，每隔三分钟上传一次
                    if (mRtBean == null)
                        return;
                    if (mBatteryBean == null)
                        mBatteryBean = new BatteryBean();
                    mBatteryBean.setBatteryVoltage(mRtBean.getBatteryVoltage());
                    OBDHttpUtil.build().BatteryVoltageDataPost(mBatteryBean);
                    break;
                case FLAG_ENABLE_RT://激活实时数据 当系统进入休眠状态 每隔十分钟唤醒一次读取实时数据
//                    rtNum = 10;//休眠状态上传数据
//                    mPortManage.addWriteData("ATRON");
//                    mHandler.sendEmptyMessageDelayed(FLAG_ENABLE_RT, 1000 * 60 * 3);
                    break;
                case FLAG_TT_START_TIME:
                    Date date = new Date();
                    if (date.getYear() < 118)
                        mHandler.sendEmptyMessageDelayed(FLAG_TT_START_TIME, 1000 * 3);
                    mTTStartTime = mSimpleDateFormat.format(date);
                    mSp.edit().putString(SharedName.CAR_START_TIME, mTTStartTime).apply();
                    TestLogUtil.log("获取行程开始时间：" + mTTStartTime);
                    break;
            }
        }
    }

    public OBDReceiveRun(Context context) {
        mContext = context;
        mHandler = new MyHandler(this);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mSp = mContext.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
        mPortManage = new SerialPortIOManage(mContext);
        mHandler.sendEmptyMessage(FLAG_PORT_CONNECT);//串口连接
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private int rtNum = 10;//实时数据10次才发送一次
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
            TestLogUtil.log(message);
            if (message.contains("-RT")) {//实时数据
                rtDataParse(message);
            } else if (message.contains("System running")) {//汽车点火
//                ignition();
            } else if (message.contains("System sleeping")) {//汽车熄火
//                flameOut();
            } else if (message.contains("Connect ECU OK")) {
                mPortManage.addWriteData("ATHBT");//获取驾驶习惯数据
                mPortManage.addWriteData("ATRON");//开启实时数据获取
            } else if (message.contains("-TT")) {//本次行程数据 系统休眠前获取一次
                Log.d(TAG, "接收到本次行程数据");
                //flameOut();
                mTtBean = new TTBean();
                mTtBean.setData(message.trim());
                TestLogUtil.log("接收到本次行程数据:" + mTtBean);
                //本次行程大于300m
                if (mTtBean.getTravelMileage() > 0.3) {
                    if (mTTStartTime == null)
                        mTTStartTime = mSp.getString(SharedName.CAR_START_TIME, "");
                    mTtBean.setStartTime(mTTStartTime);
                    mTtBean.setEndTime(mSimpleDateFormat.format(new Date()));
                    mHandler.removeMessages(FLAG_TT_POST);
                    mHandler.sendEmptyMessage(FLAG_TT_POST);
                }
            } else if (message.contains("-HBT")) {//驾驶习惯数据 ECU连接成功获取一次
                Log.d(TAG, "接收到驾驶习惯数据");
                mHbtBean = new HBTBean();
                mHbtBean.setData(message.trim());
                mHandler.removeMessages(FLAG_HBT_POST);
                mHandler.sendEmptyMessage(FLAG_HBT_POST);
            } else if (message.startsWith("$047=")) {//当前剩余油量 ECU连接成功开始 每隔60秒读取一次 熄火便不再读取
                Log.d(TAG, "接收到当前油耗：" + message);
                String remain = message.replace("$047=", "");
                if (!TextUtils.isEmpty(remain) && remain.contains("ECU not support")) {
                    mRemain = -1;
                } else if (!TextUtils.isEmpty(remain)) {
                    mRemain = Integer.parseInt(remain);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void serviceEvent(AccMessage message) {
        if (message.getEventFlag() == AccMessage.EVENT_FLAG_ACC_ON) {
            ignition();
        } else if (message.getEventFlag() == AccMessage.EVENT_FLAG_ACC_OFF) {
            flameOut();
        }
    }

    private void rtDataParse(String message) {//实时数据解析
        if (mRtBean == null)
            mRtBean = new RTBean();
        mRtBean.setData(message.trim());
        String currentEngineSpeed = mRtBean.getEngineSpeed();
        //熄火判断
        if (!TextUtils.isEmpty(currentEngineSpeed) && !TextUtils.isEmpty(mLastEngineSpeed)
                && Integer.parseInt(currentEngineSpeed) < 300 && Integer.parseInt(mLastEngineSpeed) >= 300) {
//            flameOut();//当上一次转速大于等于300，这次小于300代表熄火
        }
        mLastEngineSpeed = currentEngineSpeed;
        //点火判断
        if (!TextUtils.isEmpty(currentEngineSpeed) && Integer.parseInt(currentEngineSpeed) >= 300
                && mEngineStatus == ENGINE_STATUS_STOP) {
//            ignition();//当当前转速大于等于300，发动机前面处于熄火状态，说明发动机在启动
        }
        //实时数据10次上传一次
        rtNum++;
        if (rtNum >= 10) {
            mHandler.sendEmptyMessage(FLAG_RT_POST);
            rtNum = 0;
        }
    }

    private synchronized void ignition() {

        Log.d(TAG, "判断汽车正在点火");
        TestLogUtil.log("判断汽车正在点火");
        mHandler.sendEmptyMessage(FLAG_BATTERY_UPLOAD);//上传电池电量
        mEngineStatus = ENGINE_STATUS_START;
        mHandler.sendEmptyMessage(FLAG_TT_START_TIME);
        mHandler.removeMessages(FLAG_ON_POST);
        mHandler.sendEmptyMessage(FLAG_ON_POST);
        mEventBus.post(new CarStateMessage(CarStateMessage.CAR_FLAG_FLAG_START));
    }

    private synchronized void flameOut() {

        Log.d(TAG, "判断汽车正在熄火");
        TestLogUtil.log("判断汽车正在熄火");
        mHandler.removeMessages(FLAG_TT_START_TIME);
        mHandler.sendEmptyMessage(FLAG_BATTERY_UPLOAD);//上传电池电量
        mEngineStatus = ENGINE_STATUS_STOP;
        mHandler.removeMessages(FLAG_OFF_POST);
        mHandler.sendEmptyMessage(FLAG_OFF_POST);
        mEventBus.post(new CarStateMessage(CarStateMessage.CAR_FLAG_FLAG_STOP));
    }

    BaseHttpUtil.MyCallback mMyCallback = new BaseHttpUtil.MyCallback() {

        @Override
        public void onFailure(int tag, String errorMessage) {
            if (tag == FLAG_TT_POST)
                TestLogUtil.log("实时数据上传失败，5s后重新上传");
            else if (tag == FLAG_HBT_POST)
                TestLogUtil.log("驾驶习惯数据上传失败，5s后重新上传");
            else if (tag == FLAG_OFF_POST)
                TestLogUtil.log("熄火数据上传失败，5s后重新上传");
            else if (tag == FLAG_ON_POST)
                TestLogUtil.log("点火数据上传失败，5s后重新上传");
            mHandler.sendEmptyMessageDelayed(tag, 1000 * 5);
        }
    };

    public void addWriteData(String data) {
        mPortManage.addWriteData(data);
    }

    public boolean isConnect() {
        return mPortManage.isConnect();
    }

    public void onDestroy() {
        mPortManage.addWriteData("ATROFF");
        mPortManage.disConnect();
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(FLAG_PORT_CONNECT);
    }
}
