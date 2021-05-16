package com.xupt.mvp;

import com.xupt.constant.Constant;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class IResetPasswordImpl implements ResetPasswordContract.Model{
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public String resetPassword(String email, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
        Request registerRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.RESET_PASSWORD_PATH)
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
