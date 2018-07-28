package com.jld.obdserialport.http;

import android.util.Log;

import com.google.gson.JsonObject;
import com.jld.obdserialport.utils.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.jld.obdserialport.MyApplication.OBD_DEFAULT_ID;

public class BindHttpUtil extends BaseHttpUtil {

    public static final String TAG  = "BindHttpUtil";
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
        jsonObject.addProperty("deviceID", OBD_DEFAULT_ID);
        Log.d(TAG, "设备ID上传: " + jsonObject);
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "设备ID上传成功: " + response.message());
                } else {
                    Log.d(TAG, "设备ID上传失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
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
    public void jPushBindUpload(final int tag, String alias,String iccid, final MyCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", OBD_DEFAULT_ID);
        jsonObject.addProperty("jPushAlias", alias);
        jsonObject.addProperty("iccid", iccid);
        Log.d(TAG, "上传设备绑定信息: " + jsonObject);
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
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "上传设备绑定信息成功: " + response.message());
                } else {
                    Log.d(TAG, "上传设备绑定信息失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
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

        FormBody formBody = new FormBody.Builder().add("obdId", OBD_DEFAULT_ID).build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", OBD_DEFAULT_ID);
        RequestBody body = RequestBody.create(mJsonType, jsonObject.toString());
        Log.d(TAG, "获取设备绑定信息：" + jsonObject.toString());
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    callback.onResponse(tag, response.body().string());
                    Log.d(TAG, "获取设备绑定信息成功: " + response.message());
                } else {
                    Log.d(TAG, "获取设备绑定信息失败 onResponse: " + response.message());
                    callback.onFailure(tag, response.message());
                }
            }
        });
    }
}
