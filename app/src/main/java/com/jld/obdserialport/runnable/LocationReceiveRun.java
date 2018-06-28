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
    private LocationManager mLocationManager;
    public static GPSBean mGpsBean = new GPSBean();
    private final EventBus mEventBus;

    public LocationReceiveRun(Context context) {
        mContext = context;
        Log.d(TAG, "LocationReceiveRun");
        mEventBus = EventBus.getDefault();
        mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "LocationReceiveRun"));
        initLocation();
    }

    private void initLocation() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setBearingRequired(true);//带方向
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "LocationReceiveRun 权限错误");
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "LocationReceiveRun 权限错误"));
            return;
        }
        List<String> allProviders = mLocationManager.getAllProviders();
        for (int i = 0; i < allProviders.size(); i++) {
            Log.d(TAG, "allProviders item:" + allProviders.get(i));
        }
        if (mLocationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3, mLocationListener);
            Log.d(TAG, "GPS定位...");
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "LocationReceiveRun GPS定位"));

        } else if (mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 3, mLocationListener);
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "LocationReceiveRun 网络定位"));
            Log.d(TAG, "网络定位...");
        } else if (mLocationManager.getProvider(LocationManager.PASSIVE_PROVIDER) != null) {
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 3, mLocationListener);
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "LocationReceiveRun 其他定位"));
            Log.d(TAG, "其他定位...");
        }
    }

    LocationListener mLocationListener = new LocationListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "------------onLocationChanged: " + location);
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "onLocationChanged:"+location));

            mGpsBean.setDirection(location.getBearing());
            mGpsBean.setLatitude(location.getLatitude());
            mGpsBean.setLongitude(location.getLongitude());
            GPSHttpUtil.build().gpsDataPost(mGpsBean);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "onStatusChanged:"+status));
            Log.d(TAG, "onStatusChanged: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "onProviderEnabled"));
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            mEventBus.post(new OBDDataMessage(OBDDataMessage.CONTENT_FLAG, "onProviderDisabled"));
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
    };

    public void removeUpdates() {
        mLocationManager.removeUpdates(mLocationListener);
    }
}
