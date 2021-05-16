package com.xupt.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.Toast;

import com.xupt.app.BaseApplication;
import com.xupt.constant.Constant;

import java.io.IOException;
import java.net.ContentHandler;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SendWebDataServer extends AsyncTask<String,Integer,String> {

    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody userRequestBody = new FormBody.Builder()
                .add("userId", strings[0])
                .add("sensorData",strings[1])
                .build();

        Request updateRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.SENSOR_COLLECT)
                .post(userRequestBody)
                .build();

        String status = "";
        try {
            status = Objects.requireNonNull(okHttpClient.newCall(updateRequest).execute().body()).string();
        } catch (IOException e) {
            status = "failure";
        }
        return status;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if ("failure".equals(s)) {
            Toast.makeText(BaseApplication.Companion.getContext(), "数据存储失败，请尽快切换到Email服务器！", Toast.LENGTH_LONG).show();
        }
    }
}