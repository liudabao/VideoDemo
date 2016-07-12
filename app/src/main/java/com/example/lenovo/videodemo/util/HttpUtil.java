package com.example.lenovo.videodemo.util;

import android.util.Log;

import com.example.lenovo.videodemo.listener.OnHttpListener;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by lenovo on 2016/7/12.
 */
public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();

    public static String get(String url, OnHttpListener listener){

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                listener.onSuccess(response.body().string());
            }
            else{
                Log.e("TAG","false");
                listener.onFailed();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
