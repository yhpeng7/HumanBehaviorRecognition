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

public class IRegisterImpl implements RegisterContract.Model{
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public User register(String username, String password, String email) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("email", email)
                .build();
        Request registerRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.REGISTER_PATH)
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
