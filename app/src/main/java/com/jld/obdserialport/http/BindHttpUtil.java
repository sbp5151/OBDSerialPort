package com.jld.obdserialport.http;

import android.util.Log;

import com.google.gson.JsonObject;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.TestLogUtil;

import junit.framework.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.jld.obdserialport.MyApplication.OBD_ID;

public class BindHttpUtil extends BaseHttpUtil {

    public static final String TAG = "BindHttpUtil";
    public static BindHttpUtil mHttpUtil;

    private BindHttpUtil() {
        super();
    }

    public static BindHttpUtil build() {
        if (mHttpUtil == null)
            mHttpUtil = new BindHttpUtil();
        return mHttpUtil;
    }

    /**
     * 设备ID上传
     */
    public void uploadDeviceID(final int tag, final MyCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", OBD_ID);
        Log.d(TAG, "设备ID上传: " + jsonObject);
        TestLogUtil.log("设备ID上传: " + jsonObject);
        RequestBody body = RequestBody.create(mJsonType, jsonObject.toString());
        Request build = new Request.Builder()
                .url(Constant.URL_UPLOAD_DEVICE_ID)
                .header("sign", getSign())
                .post(body)
                .build();
        mOkHttpClient.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "设备ID上传失败 onFailure: " + e.getMessage());
                TestLogUtil.log("设备ID上传失败 onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response);
                ResponseBody responseBody = response.body();
                if (responseBody != null && response.code() == 200) {
                    String bodyString = responseBody.string();
                    callback.onResponse(tag, bodyString);
                    Log.d(TAG, "设备ID上传成功: " + bodyString);
                    TestLogUtil.log("设备ID上传成功 " + bodyString);
                } else {
                    TestLogUtil.log("设备ID上传失败 onResponse: " + response.toString());
                    callback.onFailure(tag, response.toString());
                }
            }
        });
    }

    /**
     * 上传设备绑定信息
     *
     * @param tag
     * @param alias
     * @param callback
     */
    public void jPushBindUpload(final int tag, String alias, String iccid, final MyCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", OBD_ID);
        jsonObject.addProperty("jPushAlias", alias);
        jsonObject.addProperty("iccid", iccid);
        Log.d(TAG, "上传设备绑定信息: " + jsonObject);
        TestLogUtil.log("上传设备绑定信息: " + jsonObject);
        RequestBody body = RequestBody.create(mJsonType, jsonObject.toString());
        Request build = new Request.Builder()
                .url(Constant.URL_UPLOAD_BIND_MSG)
                .header("sign", getSign())
                .post(body)
                .build();
        mOkHttpClient.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "上传设备绑定信息失败 onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: "+response);
                ResponseBody responseBody = response.body();
                if (responseBody != null && response.code() == 200) {
                    String bodyString = responseBody.string();
                    callback.onResponse(tag, bodyString);
                    Log.d(TAG, "上传设备绑定信息成功: " + bodyString);
                    TestLogUtil.log("上传设备绑定信息成功: " + bodyString);
                } else {
                    Log.d(TAG, "上传设备绑定信息失败 onResponse: " + response.toString());
                    TestLogUtil.log("上传设备绑定信息失败 onResponse:: " + response.toString());
                    callback.onFailure(tag, response.toString());
                }
            }
        });
    }

    /**
     * 获取设备绑定信息
     *
     * @param tag
     * @param callback
     */
    public void jPushBindRequest(final int tag, final MyCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", OBD_ID);
        RequestBody body = RequestBody.create(mJsonType, jsonObject.toString());
        Log.d(TAG, "获取设备绑定信息：" + jsonObject.toString());
        TestLogUtil.log("获取设备绑定信息：" + jsonObject.toString());
        Request build = new Request.Builder()
                .url(Constant.URL_REQUEST_BIND_MSG)
                .header("sign", getSign())
                .post(body)
                .build();
        mOkHttpClient.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
                Log.d(TAG, "获取设备绑定信息失败 onFailure: " + e.getMessage());
                TestLogUtil.log("获取设备绑定信息失败 onFailure:：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                Log.d(TAG, "onResponse: " + response);
                if (responseBody != null && response.code() == 200) {
                    String bodyString = responseBody.string();
                    TestLogUtil.log("获取设备绑定信息成功: " +bodyString);
                    callback.onResponse(tag, bodyString);
                    Log.d(TAG, "获取设备绑定信息成功: " + bodyString);
                } else {
                    TestLogUtil.log("获取设备绑定信息失败 onResponse: " + response.message());
                    Log.d(TAG, "获取设备绑定信息失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
                }
            }
        });
    }
}
