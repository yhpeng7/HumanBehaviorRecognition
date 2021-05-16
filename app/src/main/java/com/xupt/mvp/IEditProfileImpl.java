package com.xupt.mvp;

import android.text.TextUtils;

import com.xupt.bean.User;
import com.xupt.constant.Constant;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

class IEditProfileImpl implements EditProfileContract.Model {
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public String updateProfile(String user, String avatarPath, String avatarName,boolean compareEliminateAvatar) {
        String status;

        RequestBody userRequestBody = new FormBody.Builder()
                .add("user", user)
                .build();

        Request updateRequest = new Request.Builder()
                .url(Constant.BASE_URL + Constant.UPDATE_PROFILE)
                .post(userRequestBody)
                .build();

        try {
            status = Objects.requireNonNull(okHttpClient.newCall(updateRequest).execute().body()).string();
        } catch (IOException e) {
            status = "failure";
        }

        if (("success".equals(status) || compareEliminateAvatar) && !TextUtils.isEmpty(avatarPath)) {
            RequestBody avatar = RequestBody.create(MediaType.parse("multipart/form-data"), new File(avatarPath));
            RequestBody avatarRequestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("avatar", avatarName, avatar)
                    .build();
            Request updateAvatarRequest = new Request.Builder()
                    .url(Constant.BASE_URL + Constant.UPDATE_AVATAR)
                    .post(userRequestBody)
                    .post(avatarRequestBody)
                    .build();
            try {
                status = Objects.requireNonNull(okHttpClient.newCall(updateAvatarRequest).execute().body()).string();
            } catch (IOException e) {
                status = "avatarFailure";
            }
        }

        return status;
    }
}
