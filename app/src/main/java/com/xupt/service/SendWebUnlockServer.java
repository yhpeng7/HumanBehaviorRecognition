package com.xupt.service;

import android.os.AsyncTask;

import com.xupt.constant.Constant;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

class SendWebUnlockServer extends AsyncTask<String,Integer,String> {
    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody userRequestBody = new FormBody.Builder()
                .add("userId", strings[0])
                .add("sensorData",strings[1])
                .build();

        Request updateRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.SENSOR_UNLOCK)
                .post(userRequestBody)
                .build();

        String status = null;
        try {
            status = Objects.requireNonNull(okHttpClient.newCall(updateRequest).execute().body()).string();
        } catch (IOException e) {
            status = "failure";
        }
        return status;
    }
}
