package com.jld.obdserialport.http;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileHttpUtil extends BaseHttpUtil {

    private static FileHttpUtil fileHttpUtil;
    public static final String TAG = "FileHttpUtil";
    private FileHttpUtil() {
    }

    public static FileHttpUtil build() {
        if (fileHttpUtil == null)
            fileHttpUtil = new FileHttpUtil();
        return fileHttpUtil;
    }
    public interface DownloadFileListener {
        public void onDownloadFailed();

        public void onDownloadSucceed();

        public void onDownloadLoading(long progress);
    }

    public interface UploadFileListener {
        void onUploadFailed();
    }

    public void fileDownload(final String fileUrl, String saveFile, final DownloadFileListener listener) {
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

    public void uploadFileUtil(String uploadUrl, String filePath, final UploadFileListener listener) {
        Log.d(TAG, "文件上传：" + filePath);
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "上传文件不存在");
            return;
        }
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(RequestBody.create(mediaType, file))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "文件上传失败: " + e.toString());
                listener.onUploadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    Log.d(TAG, "文件上传成功");
                } else {
                    listener.onUploadFailed();
                    Log.d(TAG, "文件上传失败: " + response.body().string());
                }
            }
        });
    }
}
