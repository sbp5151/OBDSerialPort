package com.jld.obdserialport.runnable;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.jld.obdserialport.MyApplication;
import com.jld.obdserialport.bean.request.GPSBean;
import com.jld.obdserialport.http.GPSHttpUtil;
import com.jld.obdserialport.utils.TestLogUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 获取GPS信息
 */
public class LocationReceiveRun extends BaseRun {

    private static final String TAG = "LocationReceiveRun";
    private Context mContext;
    public static GPSBean mGpsBean = new GPSBean();
    private final EventBus mEventBus;
    private AMapLocationClient mAMapLocationClient;

    public LocationReceiveRun(Context context) {
        mContext = context;
        Log.d(TAG, "LocationReceiveRun");
        mEventBus = EventBus.getDefault();
        initLocation();
    }

    private void initLocation() {
        mAMapLocationClient = new AMapLocationClient(mContext.getApplicationContext());

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setInterval(1000*3);
//        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//GPS定位
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
        option.setNeedAddress(true);
        mAMapLocationClient.setLocationOption(option);
        mAMapLocationClient.setLocationListener(mAMapLocationListener);
        mAMapLocationClient.startLocation();
    }

    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.d(TAG, "onLocationChanged: " + aMapLocation);
//            Log.d(TAG, "getAdviseMessage: " + locationQualityReport.getAdviseMessage());
//            Log.d(TAG, "getGPSSatellites: " + locationQualityReport.getGPSSatellites());
//            Log.d(TAG, "getGPSStatus: " + locationQualityReport.getGPSStatus());
//            Log.d(TAG, "getNetUseTime: " + locationQualityReport.getNetUseTime());
//            Log.d(TAG, "getNetworkType: " + locationQualityReport.getNetworkType());
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0 && (aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_WIFI || aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_GPS)) {
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
    }
}
