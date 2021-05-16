package com.xupt.mvp;

import com.xupt.constant.Constant;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class IMainImpl implements MainContract.Model{
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public String cancelAccount(String id) {
        RequestBody formBody = new FormBody.Builder()
                .add("id", id)
                .build();
        Request registerRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.CANCEL_ACCOUNT)
                .post(formBody)
                .build();
        String status = null;
        try {
            status = Objects.requireNonNull(okHttpClient.newCall(registerRequest).execute().body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
}
