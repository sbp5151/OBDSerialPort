package com.jld.obdserialport.http;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.JsonObject;
import com.jld.obdserialport.MyApplication;
import com.jld.obdserialport.event_msg.TestDataMessage;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.TestLogUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class OtherHttpUtil extends BaseHttpUtil {

    public static final String TAG = "OtherHttpUtil";
    private static OtherHttpUtil otherHttpUtil;

    private OtherHttpUtil() {
    }

    public static OtherHttpUtil build() {
        if (otherHttpUtil == null)
            otherHttpUtil = new OtherHttpUtil();
        return otherHttpUtil;
    }

    /**
     * APK检测更新
     *
     * @param context
     */
    public void checkApkUpdate(Context context, final ApkCheckUpdateListener updateListener) {
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            JsonObject json = new JsonObject();
            json.addProperty("versionCode", versionName);
            Log.d(TAG, "checkApkUpdate json: " + json.toString());
            RequestBody requestBody = RequestBody.create(mJsonType, json.toString());
            final Request request = new Request.Builder()
                    .url(Constant.URL_CHECK_APK_UPDATE)
                    .header("sign", getSign())
                    .post(requestBody)
                    .build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "APK检测更新访问失败：" + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null && response.code() == 200) {
                        try {
                            JSONObject json = new JSONObject(responseBody.string());
                            if (json.getInt("flag") == 1) {
                                String apkDownUrl = json.getString("apkDownUrl");
                                if (!TextUtils.isEmpty(apkDownUrl)) {
//                                fileDownload(apkDownUrl);
                                    File carFuture = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CarFuture");
                                    if (carFuture.exists()) {
                                        String[] listName = carFuture.list();
                                        String apkName = apkDownUrl.substring(apkDownUrl.lastIndexOf("/") + 1).replace(".1", "");
                                        for (String name : listName) {
                                            if (name.equals(apkName)) {//文件已下载，直接安装
                                                updateListener.onApkInstall(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                                        File.separator + "CarFuture" + File.separator + name);
                                                return;
                                            }
                                        }
                                    }
                                    //需下载
                                    updateListener.onApkDownload(apkDownUrl);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else
                        TestLogUtil.log("APK升级检测失败" + response.toString());
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deviceOnlineUpdate() {
        JsonObject jo = new JsonObject();
        jo.addProperty("obdId", MyApplication.OBD_ID);
        RequestBody body = RequestBody.create(mJsonType, jo.toString());
        Request request = new Request.Builder()
                .url(Constant.URL_DEVICE_ONLINE)
                .header("sign", getSign())
                .post(body).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                EventBus.getDefault().post(new TestDataMessage("在线更新失败" + e.toString()));
                TestLogUtil.log("在线更新失败" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody != null && response.code() == 200) {
                    TestLogUtil.log("在线更新成功:" + responseBody.string());
                } else{
                    TestLogUtil.log("在线更新失败" + response.toString());
                }
            }
        });
    }

    public interface ApkCheckUpdateListener {

        public void onApkDownload(String downloadPath);

        public void onApkInstall(String installPath);
    }
}
