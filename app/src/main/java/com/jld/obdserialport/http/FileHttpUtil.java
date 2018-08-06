package com.jld.obdserialport.http;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jld.obdserialport.event_msg.TestDataMessage;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.TestLogUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileHttpUtil extends BaseHttpUtil {

    private static FileHttpUtil fileHttpUtil;
    public static final String TAG = "FileHttpUtil";

    private FileHttpUtil() {
        super();
    }

    public static FileHttpUtil build() {
        if (fileHttpUtil == null)
            fileHttpUtil = new FileHttpUtil();
        return fileHttpUtil;
    }

    public interface DownloadFileListener {
        void onDownloadFailed();

        void onDownloadSucceed();

        void onDownloadLoading(long progress);
    }

    public interface UploadFileListener {
        void onUploadFailed(String errorMessage);

        void onUploadSucceed(String message);
    }

    public void fileDownload(final String fileUrl, final DownloadFileListener listener) {
        Log.d(TAG, "文件下载: " + fileUrl);
        Request request = new Request.Builder().url(fileUrl).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) {
                String apkName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1).replace(".1", "");
                File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CarFuture" + File.separator + apkName);
                if (!saveFile.getParentFile().exists())
                    saveFile.getParentFile().mkdirs();
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    fos = new FileOutputStream(saveFile);
                    is = response.body().byteStream();
                    long totalLen = response.body().contentLength();
                    Log.d(TAG, "totalLen: " + totalLen);
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

    public void photoUploadUtil(String obdId, String uid, String fileName, String filePath, final UploadFileListener listener) {
        File file = new File(filePath);
        TestLogUtil.log("相册上传 " + filePath);

        if (!file.exists()) {
            TestLogUtil.log("相册文件不存在 " + filePath);
            return;
        }
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("obdId", obdId)
                .addFormDataPart("fileName", fileName)
                .addFormDataPart("uid", uid)
                .addFormDataPart("fileType", "1")
                .build();
        Log.d(TAG, "fileName: " + fileName);
        Log.d(TAG, "obdId: " + obdId);
        Log.d(TAG, "uid: " + uid);
        final Request request = new Request.Builder()
                .url(Constant.URL_MEDIA_UPLOAD)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onUploadFailed(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (response.code() == 200 && responseBody != null) {
                    listener.onUploadSucceed(responseBody.string());
                } else {
                    listener.onUploadFailed(response.message());
                }
            }
        });
    }

    public void videoUploadUtil(String obdId, String uid, String fileName, String filePath, final UploadFileListener listener) {
//        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarFuture" + File.separator + "future.mp4";
        File file = new File(filePath);
        TestLogUtil.log("视频文件上传 " + filePath);

        if (!file.exists()) {
            Log.d(TAG, "文件不存在: " + filePath);
//            EventBus.getDefault().post(new TestDataMessage("文件不存在: " + filePath));
            TestLogUtil.log("文件不存在: " + filePath);
            return;
        }
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("obdId", obdId)
                .addFormDataPart("fileName", fileName)
                .addFormDataPart("uid", uid)
                .addFormDataPart("fileType", "2")
                .build();
        Log.d(TAG, "filePath: " + filePath);
        Log.d(TAG, "fileName: " + fileName);
        Log.d(TAG, "obdId: " + obdId);
        Log.d(TAG, "uid: " + uid);
//        EventBus.getDefault().post(new TestDataMessage("filepath:" + filePath + "   filename:" + fileName
//                + "  obdid:" + obdId + " uid:" + uid));
        TestLogUtil.log("filepath:" + filePath + "   filename:" + fileName
                + "  obdid:" + obdId + " uid:" + uid);
        Request request = new Request.Builder()
                .url(Constant.URL_MEDIA_UPLOAD)
                .post(body)
                .header("sign", getSign())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onUploadFailed(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                EventBus.getDefault().post(new TestDataMessage("视频上传访问成功" + response.body().string()));
                ResponseBody responseBody = response.body();
                if (responseBody != null && response.code() == 200) {
                    listener.onUploadSucceed(responseBody.string());
                } else if (responseBody != null) {
                    listener.onUploadFailed(responseBody.string());
                }
            }
        });
    }

    public void uploadFileUtil(String uploadUrl, String filePath, final UploadFileListener listener) {
//        Log.d(TAG, "文件上传：" + filePath);
//        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
//        File file = new File(filePath);
//        if (!file.exists()) {
//            Log.e(TAG, "上传文件不存在");
//            return;
//        }
//        Request request = new Request.Builder()
//                .url(uploadUrl)
//                .post(RequestBody.create(mediaType, file))
//                .build();
//
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d(TAG, "文件上传失败: " + e.toString());
//                listener.onUploadFailed();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.code() == 200) {
//                    Log.d(TAG, "文件上传成功");
//                    listener.onUploadSucceed();
//                } else {
//                    listener.onUploadFailed();
//                    Log.d(TAG, "文件上传失败: " + response.body().string());
//                }
//            }
//        });
    }
}
