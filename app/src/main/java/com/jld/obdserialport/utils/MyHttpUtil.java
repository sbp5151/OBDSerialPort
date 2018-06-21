package com.jld.obdserialport.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jld.obdserialport.bean.HBTBean;
import com.jld.obdserialport.bean.PIDBeanTest;
import com.jld.obdserialport.bean.StartOrStopBean;
import com.jld.obdserialport.bean.TTBean;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyHttpUtil {

    private static final String TAG = "MyHttpUtil";
    private static MyHttpUtil mHttpUtil;
    private OkHttpClient mOkHttpClient;
    private final MediaType mJsonType;
    private final Gson mGson;

    private MyHttpUtil() {
        mOkHttpClient = new OkHttpClient();
        mJsonType = MediaType.parse("application/json; charset=utf-8");
        mGson = new Gson();
    }

    public static MyHttpUtil build() {
        if (mHttpUtil == null)
            mHttpUtil = new MyHttpUtil();
        return mHttpUtil;
    }

    public void pidDataPost(PIDBeanTest pidBean) {
        String pidJson = mGson.toJson(pidBean);
        Log.d(TAG, "pidDataPost pidJson: " + pidJson);
        RequestBody body = RequestBody.create(mJsonType, pidJson);
        Request request = new Request.Builder()
                .url(Constant.URL_PID_POST)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new ObdCallback());
    }

    public void hbtDataPost(HBTBean bean) {
        String hbtJson = mGson.toJson(bean);
        Log.d(TAG, "hbtDataPost hbtJson: " + hbtJson);
        RequestBody body = RequestBody.create(mJsonType, hbtJson);
        final Request request = new Request.Builder()
                .url(Constant.URL_HBT_POST)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new ObdCallback());
    }

    public void ttDataPost(TTBean bean) {
        String ttJson = mGson.toJson(bean);
        Log.d(TAG, "ttDataPost ttJson: " + ttJson);
        RequestBody body = RequestBody.create(mJsonType, ttJson);
        final Request request = new Request.Builder()
                .url(Constant.URL_TT_POST)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new ObdCallback());
    }

    /**
     * 点火熄火上传
     *
     * @param bean
     */
    public void carStartOrStopPost(StartOrStopBean bean) {
        String startOrStopJson = mGson.toJson(bean);
        Log.d(TAG, "carStartOrStopPost: " + startOrStopJson);
        RequestBody body = RequestBody.create(mJsonType, startOrStopJson);
        Request build = new Request.Builder()
                .url(Constant.URL_CAR_ONOFF_POST)
                .post(body)
                .build();
        mOkHttpClient.newCall(build).enqueue(new ObdCallback());
    }

    /**
     * 设备ID上传
     */
    public void uploadDeviceID(final int tag, final MyCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", Constant.OBD_DEFAULT_ID);
        Log.d(TAG, "jPushBindUpload: " + jsonObject);
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "uploadDeviceID tag= " + tag);

                if (response.code() == 200)
                    callback.onResponse(tag, response.body().string());
                else callback.onFailure(tag, response.message());
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
    public void jPushBindUpload(final int tag, String alias, final MyCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", Constant.OBD_DEFAULT_ID);
        jsonObject.addProperty("jPushAlias", alias);
        Log.d(TAG, "jPushBindUpload: " + jsonObject);
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200)
                    callback.onResponse(tag, response.body().string());
                else callback.onFailure(tag, response.message());
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

        FormBody formBody = new FormBody.Builder().add("obdId", Constant.OBD_DEFAULT_ID).build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", Constant.OBD_DEFAULT_ID);
        RequestBody body = RequestBody.create(mJsonType, jsonObject.toString());
        Log.d(TAG, "jPushBindRequest: body = " + jsonObject.toString());
        Request build = new Request.Builder()
                .url(Constant.URL_REQUEST_BIND_MSG)
                .header("sign", getSign())
                .post(body)
                .build();
        mOkHttpClient.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(tag, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200)
                    callback.onResponse(tag, response.body().string());
                else callback.onFailure(tag, response.message());
            }
        });
    }


    private String getSign() {
        String sign = "";
        try {
            //加密字符串
            sign = JavaAESCryptor.encrypt(System.currentTimeMillis() + "", Constant.SIGN_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSign: " + sign);
        return sign;
    }

    public interface MyCallback {
        void onFailure(int tag, String errorMessage);

        void onResponse(int tag, String body);
    }

    class ObdCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "网络访问失败：" + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                Log.d(TAG, "body:" + response.body().string());
            } else {
                Log.e(TAG, "网络状态异常：" + response);
            }
        }
//        public abstract void requestSucceed(ResponseBody body);
    }
}
