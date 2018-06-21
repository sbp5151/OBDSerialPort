package com.jld.obdserialport.runnable;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jld.obdserialport.R;
import com.jld.obdserialport.SerialPortIOManage;
import com.jld.obdserialport.bean.HBTBean;
import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.bean.PIDBeanTest;
import com.jld.obdserialport.bean.RTBean;
import com.jld.obdserialport.bean.StartOrStopBean;
import com.jld.obdserialport.bean.TTBean;
import com.jld.obdserialport.utils.MyHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import static com.jld.obdserialport.utils.Constant.SERIAL_PORT_BAUD_RATE;
import static com.jld.obdserialport.utils.Constant.SERIAL_PORT_PATH;

public class OBDReceiveRun {

    public static final String TAG = "OBDReceiveRun";
    private Context mContext;
    private final MyHandler mHandler;
    //串口连接标识
    private final int PORT_CONNECT_FLAG = 0x01;
    private final int LOOP_GET_PID = 0x02;
    private final int TEST_DATA = 0x03;
    private SerialPortIOManage mPortManage;
    String[] mPidCode;
    private RTBean mRtBean;
    int testNum = 0;
    private PIDBeanTest mPidBean;
    private double mLatitude = 81;
    private double mLongitude = 61;
    private float mBearing = 11;

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
            if (msg.what == PORT_CONNECT_FLAG) {//串口连接
                Log.d(TAG, "connect: 串口连接：" + SERIAL_PORT_PATH + " -- " + SERIAL_PORT_BAUD_RATE);
                mPortManage.connect(SERIAL_PORT_PATH, SERIAL_PORT_BAUD_RATE);
            } else if (msg.what == LOOP_GET_PID) {
                for (int i = 0; i < mPidCode.length; i++) {
                    mPortManage.addWriteData(mPidCode[i]);
                }
//                mHandler.sendEmptyMessageDelayed(LOOP_GET_PID, 1000 * 60 * 2);
                mHandler.sendEmptyMessageDelayed(LOOP_GET_PID, 1000 * 15);
            } else if (msg.what == TEST_DATA) {
                if (testNum % 3 == 0)
                    EventBus.getDefault().post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "System running"));
                else if (testNum % 3 == 1)
                    EventBus.getDefault().post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "$OBD-TTfadsfsad"));
                else if (testNum % 3 == 2)
                    EventBus.getDefault().post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "System sleeping"));
                mHandler.sendEmptyMessageDelayed(TEST_DATA, 1000 * 60);//串口连接
                testNum++;
            }
        }
    }

    public OBDReceiveRun(Context context) {
        mContext = context;
        mHandler = new MyHandler(this);
        EventBus.getDefault().register(this);

        mPidCode = mContext.getResources().getStringArray(R.array.PIDCode);
        mPortManage = new SerialPortIOManage(mContext);
        mHandler.sendEmptyMessage(PORT_CONNECT_FLAG);//串口连接
//        mHandler.sendEmptyMessageDelayed(TEST_DATA, 10000);//串口连接
    }

    public void addWriteData(String data) {
        mPortManage.addWriteData(data);
    }

    public boolean isConnect() {
        return mPortManage.isConnect();
    }

    public void disConnect() {
        mPortManage.disConnect();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationEvent(Location location) {
        Log.d(TAG, "locationEvent:" + location);
        mLatitude = location.getLatitude();//纬度
        mLongitude = location.getLongitude();//经度
        mBearing = location.getBearing();//方向
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void odbEvent(OBDDataMessage messageEvent) {
        if (messageEvent.getFlag() == OBDDataMessage.CONNECT_STATE_FLAG) {
            if (messageEvent.isConnect()) {
                Log.d(TAG, "connect: 串口连接成功！！！");
                mHandler.sendEmptyMessageDelayed(LOOP_GET_PID, 1000);//获取PID数据
            } else {
                Log.d(TAG, "connect: 串口连接失败！！！");
                mHandler.sendEmptyMessageDelayed(PORT_CONNECT_FLAG, 3000);//3s重连
            }
        } else if (messageEvent.getFlag() == OBDDataMessage.CONTENT_FLAG) {

            String message = messageEvent.getMessage();
            Log.d(TAG, "odbEvent message:" + message);
            if (message.startsWith("$OBDRT=")) {//实时数据
                if (mRtBean == null)
                    mRtBean = new RTBean();
                mRtBean.setData(message.replace("$OBDRT=", ""));
                Log.d(TAG, "odbEvent: " + mRtBean);

            } else if (message.startsWith("PID")) {//PID数据

                if (mPidBean == null)
                    mPidBean = new PIDBeanTest();
                if (mPidBean.setData(message.trim())) {//数据获取完成
                    Log.d(TAG, "PID数据获取完成");
                    MyHttpUtil.build().pidDataPost(mPidBean);
                }
            } else if (message.contains("System running")) {//汽车点火
                Log.d(TAG, "汽车点火");
                StartOrStopBean startOrStopBean = new StartOrStopBean(StartOrStopBean.CAR_START, 1, 1);
                MyHttpUtil.build().carStartOrStopPost(startOrStopBean);

            } else if (message.contains("System sleeping")) {//汽车熄火
                Log.d(TAG, "汽车熄火");
                StartOrStopBean startOrStopBean = new StartOrStopBean(StartOrStopBean.CAR_STOP, 1, 1);
                MyHttpUtil.build().carStartOrStopPost(startOrStopBean);

            } else if (message.startsWith("$OBD-TT")) {//本次行程数据
                Log.d(TAG, "本次行程数据");
                TTBean ttBean = new TTBean();
                ttBean.setData(message);
                //本次行程大于等于1km
                if (ttBean.getTravelMileage() >= 1) {
                    //获取驾驶习惯数据
                    mPortManage.addWriteData("ATHBT");
                    MyHttpUtil.build().ttDataPost(ttBean);
                }
//            } else if (message.startsWith("$OBD-HBT")) {//驾驶习惯数据
            } else if (message.startsWith("POBD-HBT")) {//驾驶习惯数据
                Log.d(TAG, "驾驶习惯数据");
                HBTBean hbtBean = new HBTBean();
                hbtBean.setData(message);
                MyHttpUtil.build().hbtDataPost(hbtBean);
            }
        }
    }
}
