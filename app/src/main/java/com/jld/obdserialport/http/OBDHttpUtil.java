package com.jld.obdserialport.http;

import android.util.Log;

import com.jld.obdserialport.bean.ATBeanTest;
import com.jld.obdserialport.bean.HBTBean;
import com.jld.obdserialport.bean.OnOrOffBean;
import com.jld.obdserialport.bean.RTBean;
import com.jld.obdserialport.bean.TTBean;
import com.jld.obdserialport.utils.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OBDHttpUtil extends BaseHttpUtil {

    public static final String TAG = "OBDHttpUtil";
    public static OBDHttpUtil mHttpUtil;

    private OBDHttpUtil() {
        super();
    }

    public static OBDHttpUtil build() {
        if (mHttpUtil == null)
            mHttpUtil = new OBDHttpUtil();
        return mHttpUtil;
    }


    /**
     * 实时数据上传
     *
     * @param rtBean
     */
    public void rtDataPost(RTBean rtBean, final int tag, final MyCallback callback) {
        String rtJson = mGson.toJson(rtBean);
        Log.d(TAG, "实时数据上传: " + rtJson);
        RequestBody body = RequestBody.create(mJsonType, rtJson);
        Request request = new Request.Builder()
                .url(Constant.URL_RT_POST)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "实时数据上传失败 onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "实时数据上传成功: " + response.message());
                } else {
                    Log.d(TAG, "实时数据上传失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
                }
            }
        });
    }

    /**
     * 自定义数据上传
     *
     * @param pidBean
     */
    public void atDataPost(ATBeanTest pidBean) {
        String pidJson = mGson.toJson(pidBean);
        Log.d(TAG, "自定义数据上传: " + pidJson);
        RequestBody body = RequestBody.create(mJsonType, pidJson);
        Request request = new Request.Builder()
                .url(Constant.URL_PID_POST)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "自定义数据上传失败 onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200)
                    Log.d(TAG, "自定义数据上传成功 onResponse: " + response.body().string());
                else
                    Log.d(TAG, "自定义数据上传失败 onResponse: " + response.body().string());
            }
        });
    }

    /**
     * 驾驶习惯数据上传
     *
     * @param bean
     */
    public void hbtDataPost(HBTBean bean, final int tag, final MyCallback callback) {
        String hbtJson = mGson.toJson(bean);
        Log.d(TAG, "驾驶习惯数据上传: " + hbtJson);
        RequestBody body = RequestBody.create(mJsonType, hbtJson);
        final Request request = new Request.Builder()
                .url(Constant.URL_HBT_POST)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "驾驶习惯数据上传失败 onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "驾驶习惯数据上传成功: " + response.message());
                } else {
                    Log.d(TAG, "驾驶习惯数据上传失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
                }
            }
        });
    }

    /**
     * 本次数据上传
     *
     * @param bean
     */
    public void ttDataPost(TTBean bean, final int tag, final MyCallback callback) {
        String ttJson = mGson.toJson(bean);
        Log.d(TAG, "本次数据上传: " + ttJson);
        RequestBody body = RequestBody.create(mJsonType, ttJson);
        final Request request = new Request.Builder()
                .url(Constant.URL_TT_POST)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "驾驶习惯数据上传失败 onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "本次数据上传成功: " + response.message());
                } else {
                    Log.d(TAG, "本次数据上传失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
                }
            }
        });
    }

    /**
     * 点火熄火上传
     *
     * @param bean
     */
    public void carStartOrStopPost(OnOrOffBean bean, final int tag, final MyCallback callback) {
        String startOrStopJson = mGson.toJson(bean);
        Log.d(TAG, "点火熄火上传: " + startOrStopJson);
        RequestBody body = RequestBody.create(mJsonType, startOrStopJson);
        Request build = new Request.Builder()
                .url(Constant.URL_CAR_ONOFF_POST)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "点火熄火上传失败 onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "点火熄火上传成功: " + response.message());
                } else {
                    Log.d(TAG, "点火熄火上传失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
                }
            }
        });
    }
}
