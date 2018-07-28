package com.jld.obdserialport.http;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jld.obdserialport.utils.Constant;

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
        void onDownloadFailed();

        void onDownloadSucceed();

        void onDownloadLoading(long progress);
    }

    public interface UploadFileListener {
        void onUploadFailed(String errorMessage);

        void onUploadSucceed();
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
        if (!file.exists())
            return;
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
                if (response.code() == 200) {
                    listener.onUploadSucceed();
                } else {
                    listener.onUploadFailed(response.message());
                }
            }
        });
    }

    public void videoUploadUtil(String obdId, String uid, String fileName, String filePath, final UploadFileListener listener) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.d(TAG, "文件不存在: " + filePath);
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
                if (response.code() == 200) {
                    listener.onUploadSucceed();
                } else {
                    listener.onUploadFailed(response.body().string());
                }
            }
        });
    }
    /**
     * android上传文件到服务器
     * <p/>
     * 请求的rul
     *
     * @return 返回响应的内容
     */
    public  void uploadFile2(String imgPath,UploadFileListener listener) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        // String ss = getHeadiconPath();
        try {
            URL url = new URL(Constant.URL_MEDIA_UPLOAD);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();// http连接
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(1024 * 1024);// 1M
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 保持一直连接
            httpURLConnection.setRequestProperty("Charset", "UTF-8");// 编码
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);// POST传递过去的编码

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());// 输出流
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + imgPath.substring(
                    imgPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(imgPath);// 文件输入流，写入到内存中
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();// http输入，即得到返回的结果
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            dos.close();
            is.close();
            if (!TextUtils.isEmpty(result)) {
                JSONObject json = new JSONObject(result);
                String result1 = json.getString("result");
                String iamgurl = json.getString("msg");
                if(result1.equals("0")){
                    listener.onUploadSucceed();
                }else{
                    listener.onUploadFailed(result1+iamgurl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onUploadFailed(e.getLocalizedMessage());
        }
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
