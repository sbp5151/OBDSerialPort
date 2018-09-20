package com.jld.obdserialport.runnable;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jld.obdserialport.MyApplication;
import com.jld.obdserialport.bean.request.GPSBean;
import com.jld.obdserialport.event_msg.CarStateMessage;
import com.jld.obdserialport.http.GPSHttpUtil;
import com.jld.obdserialport.utils.TestLogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 获取GPS信息
 */
public class LocationReceiveRun extends BaseRun {

    private static final String TAG = "LocationReceiveRun";
    private Context mContext;
    public static GPSBean mGpsBean = new GPSBean();
    private AMapLocationClient mAMapLocationClient;

    public LocationReceiveRun(Context context) {
        mContext = context;
        Log.d(TAG, "LocationReceiveRun");
        EventBus.getDefault().register(this);
        initLocation();
    }

    private void initLocation() {
        mAMapLocationClient = new AMapLocationClient(mContext.getApplicationContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setInterval(1000 * 3);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//GPS定位
//        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
        option.setNeedAddress(true);
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);//出行模式
        mAMapLocationClient.setLocationOption(option);
        mAMapLocationClient.setLocationListener(mAMapLocationListener);
        mAMapLocationClient.startLocation();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void serviceEvent(CarStateMessage message) {
        if (message.getFlag() == CarStateMessage.CAR_FLAG_FLAG_START) {
            if (mAMapLocationClient != null)
                mAMapLocationClient.startLocation();
        } else if (message.getFlag() == CarStateMessage.CAR_FLAG_FLAG_STOP) {
            if (mAMapLocationClient != null)
                mAMapLocationClient.stopLocation();
        }
    }

    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.d(TAG, "onLocationChanged: " + aMapLocation);
            TestLogUtil.log("定位精度: " + aMapLocation.getAccuracy()+"     定位错误代码: " + aMapLocation.getErrorCode());
//            switch (aMapLocation.getGpsAccuracyStatus()) {
//                case AMapLocation.GPS_ACCURACY_BAD:
//                    TestLogUtil.log("GPS信号弱");
//                    break;
//                case AMapLocation.GPS_ACCURACY_GOOD:
//                    TestLogUtil.log("GPS信号强");
//                    break;
//                case AMapLocation.GPS_ACCURACY_UNKNOWN:
//                    TestLogUtil.log("GPS信号未知");
//                    break;
//            }
//            TestLogUtil.log("卫星数量: " + aMapLocation.getSatellites());
            if (aMapLocation.getErrorCode() == 0 && aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_GPS
                    && aMapLocation.getAccuracy() < 10) {
                if (mGpsBean == null)
                    mGpsBean = new GPSBean();
//                mEventBus.post(new TestDataMessage(aMapLocation.getAddress() + " type:" + aMapLocation.getLocationType()));
                mGpsBean.setDirection(aMapLocation.getBearing());
                mGpsBean.setLatitude(aMapLocation.getLatitude());
                mGpsBean.setLongitude(aMapLocation.getLongitude());
                mGpsBean.setAddress(aMapLocation.getAddress());
                mGpsBean.setObdId(MyApplication.OBD_ID);
                GPSHttpUtil.build().gpsDataPost(mGpsBean);
            }
        }
    };

    public void onDestroy() {
        mAMapLocationClient.stopLocation();
        mAMapLocationClient.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
