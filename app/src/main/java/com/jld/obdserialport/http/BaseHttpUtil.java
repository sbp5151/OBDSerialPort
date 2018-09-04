package com.jld.obdserialport.http;

import android.util.Log;

import com.google.gson.Gson;
import com.jld.obdserialport.utils.Constant;
import com.jld.obdserialport.utils.JavaAESCryptor;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class BaseHttpUtil {

    protected OkHttpClient mOkHttpClient;
    protected final MediaType mJsonType;
    protected final Gson mGson;

    BaseHttpUtil() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        mJsonType = MediaType.parse("application/json; charset=utf-8");
        mGson = new Gson();
    }

    protected String getSign() {
        String sign = "";
        try {
            //加密字符串
            sign = JavaAESCryptor.encrypt(System.currentTimeMillis() + "", Constant.SIGN_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    public interface MyCallback {
        void onFailure(int tag, String errorMessage);
    }  public interface MyCallback2 {
        void onFailure(int tag, String errorMessage);
        void onResponse(int tag,String body);
    }
}
