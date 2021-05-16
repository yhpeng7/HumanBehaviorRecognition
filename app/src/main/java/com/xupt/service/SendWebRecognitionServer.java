package com.xupt.service;

import android.os.AsyncTask;

import com.xupt.constant.Constant;
import com.xupt.event.RecognitionEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

class SendWebRecognitionServer extends AsyncTask<String,Integer,String> {
    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody userRequestBody = new FormBody.Builder()
                .add("userId", strings[0])
                .add("sensorData",strings[1])
                .build();

        Request updateRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.SENSOR_RECOGNITION)
                .post(userRequestBody)
                .build();

        String status;
        try {
            status = Objects.requireNonNull(okHttpClient.newCall(updateRequest).execute().body()).string();
        } catch (IOException e) {
            status = "failure";
        }
        if (status == null || "failure".equals(status) || Integer.parseInt(status) == 5) {
            EventBus.getDefault().post(new RecognitionEvent(5));
        } else {
            EventBus.getDefault().post(new RecognitionEvent(Integer.parseInt(status)));
        }
        return status;
    }
}
