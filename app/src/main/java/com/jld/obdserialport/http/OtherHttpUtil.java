package com.jld.obdserialport.http;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jld.obdserialport.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
            Log.d(TAG, "versionName: " + versionName);
            json.addProperty("versionCode", versionName);
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
                    Log.d(TAG, "APK检测更新访问成功：" + response);
                    if (response.code() == 200) {
                        JsonParser parser = new JsonParser();
                        JsonObject object = (JsonObject) parser.parse(response.body().string());  //创建JsonObject对象
                        int flag = object.get("flag").getAsInt();
                        if (flag == 1) {
                            String apkDownUrl = object.get("apkDownUrl").getAsString();
                            if (!TextUtils.isEmpty(apkDownUrl)) {
//                                fileDownload(apkDownUrl);
                                File carFuture = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CarFuture");
                                if (carFuture.exists()) {

                                    String[] listName = carFuture.list();
                                    String apkName = apkDownUrl.substring(apkDownUrl.lastIndexOf("/") + 1);
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
                    }
                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface ApkCheckUpdateListener {

        public void onApkDownload(String downloadPath);

        public void onApkInstall(String installPath);
    }

    public interface DownloadFileListener {
        public void onDownloadFailed();
        public void onDownloadSucceed();

        public void onDownloadLoading(long progress);

    }

    public void fileDownload(final String fileUrl,String saveFile, final DownloadFileListener listener) {

        Request request = new Request.Builder().url(fileUrl).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) {
                String apkName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CarFuture" + File.separator + apkName);
                if (!saveFile.getParentFile().exists())
                    saveFile.getParentFile().mkdirs();
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    fos = new FileOutputStream(saveFile);
                    is = response.body().byteStream();
                    long totalLen = response.body().contentLength();
                    byte[] buf = new byte[1024 * 3];
                    int len = 0;
                    int sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        long progress = (sum * 100 / totalLen);
                        listener.onDownloadLoading(progress);
                    }
                    fos.flush();
                    listener.onDownloadSucceed();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
