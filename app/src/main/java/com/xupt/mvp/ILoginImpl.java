package com.xupt.mvp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xupt.bean.User;
import com.xupt.constant.Constant;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ILoginImpl implements LoginContract.Model{
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public User login(String account, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("password", password)
                .build();
        Request registerRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.LOGIN_PATH)
                .post(formBody)
                .build();
        User user = null;
        try {
            String response = Objects.requireNonNull(okHttpClient.newCall(registerRequest).execute().body()).string();
            JSONObject userJson = JSON.parseObject(response);
            user = JSON.parseObject(userJson.toString(), new TypeReference<User>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
