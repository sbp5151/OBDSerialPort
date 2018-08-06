package com.jld.obdserialport.http;

import android.util.Log;

import com.jld.obdserialport.bean.GPSBean;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.TestLogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GPSHttpUtil extends BaseHttpUtil {

    public static final String TAG = "GPSHttpUtil";
    public static GPSHttpUtil mHttpUtil;

    private GPSHttpUtil() {
        super();
    }

    public static GPSHttpUtil build() {
        if (mHttpUtil == null)
            mHttpUtil = new GPSHttpUtil();
        return mHttpUtil;
    }


    /**
     * GPS数据上传
     *
     * @param gpsBean
     */
    public void gpsDataPost(GPSBean gpsBean) {
        String gpsJson = mGson.toJson(gpsBean);
        Log.d(TAG, "GPS数据上传: " + gpsJson);
        TestLogUtil.log("GPS数据上传: " + gpsJson);
        RequestBody body = RequestBody.create(mJsonType, gpsJson);
        Request request = new Request.Builder()
                .url(Constant.URL_GPS_POST)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "GPS数据上传失败 onFailure: " + e.toString());
                TestLogUtil.log("GPS数据上传失败 onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (response.code() == 200 && responseBody != null) {
                    TestLogUtil.log("GPS数据上传成功 onResponse: " + responseBody.string());
                    Log.d(TAG, "GPS数据上传成功 onResponse: " + responseBody.string());
                } else if (responseBody != null) {
                    TestLogUtil.log("GPS数据上传成功 onResponse: " + responseBody.string());
                    Log.d(TAG, "GPS数据上传成功 onResponse: " + responseBody.string());
                }
            }
        });
    }
}