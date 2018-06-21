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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 获取GPS信息
 */
public class LocationReceiveRun {

    private static final String TAG = "LocationReceiveRun";
    private Context mContext;
    private LocationManager mLocationManager;

    public LocationReceiveRun(Context context) {
        mContext = context;
        initLocation();
    }

    private void initLocation() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setBearingRequired(true);//带方向
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<String> allProviders = mLocationManager.getAllProviders();
        for (int i = 0; i < allProviders.size(); i++) {
            Log.d(TAG, "allProviders item:" + allProviders.get(i));
        }
        if (mLocationManager.getProvider(LocationManager.GPS_PROVIDER) != null)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
        else if (mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null)
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, mLocationListener);
        else if (mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null)
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, mLocationListener);
    }

    LocationListener mLocationListener = new LocationListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            EventBus.getDefault().post(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
    };

    public void removeUpdates(){
        mLocationManager.removeUpdates(mLocationListener);
    }
}
