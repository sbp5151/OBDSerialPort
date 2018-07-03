package com.jld.obdserialport.runnable;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jld.obdserialport.bean.GPSBean;
import com.jld.obdserialport.event_msg.OBDDataMessage;
import com.jld.obdserialport.http.GPSHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 获取GPS信息
 */
public class LocationReceiveRun {

    private static final String TAG = "LocationReceiveRun";
    private Context mContext;
    public static GPSBean mGpsBean;
    private final EventBus mEventBus;
    private AMapLocationClient mAMapLocationClient;

    public LocationReceiveRun(Context context) {
        mContext = context;
        Log.d(TAG, "LocationReceiveRun");
        mEventBus = EventBus.getDefault();
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "LocationReceiveRun"));
        initLocation();
    }

    private void initLocation() {
        mAMapLocationClient = new AMapLocationClient(mContext.getApplicationContext());

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setInterval(2000);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//GPS定位
        option.setNeedAddress(true);

        mAMapLocationClient.setLocationOption(option);
        mAMapLocationClient.setLocationListener(mAMapLocationListener);
        mAMapLocationClient.startLocation();
    }

    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.d(TAG, "onLocationChanged: " + aMapLocation);
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                if (mGpsBean == null)
                    mGpsBean = new GPSBean();
                mGpsBean.setDirection(aMapLocation.getBearing());
                mGpsBean.setLatitude(aMapLocation.getLatitude());
                mGpsBean.setLongitude(aMapLocation.getLongitude());
                mGpsBean.setAddress(aMapLocation.getAddress());
                GPSHttpUtil.build().gpsDataPost(mGpsBean);
            }
        }
    };

    public void removeUpdates() {
        mAMapLocationClient.stopLocation();
        mAMapLocationClient.onDestroy();
    }
}
