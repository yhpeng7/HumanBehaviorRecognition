package com.xupt.mvp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.Objects;

class EditProfilePresenter<V extends EditProfileContract.View> extends EditProfileContract.Present<V>{
    private final IEditProfileImpl editProfileModel = new IEditProfileImpl();

    private final IMainImpl mainModel = new IMainImpl();
    private String status;

    private final Handler mMyHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Objects.requireNonNull(getMView()).hideLoading();
            if (msg.what == -1) {
                getMView().updateFailure(status);
            } else if (msg.what == 0) {
                getMView().updateSuccess();
            }
        }
    };

    @Override
    void updateProfile(String user, String avatarPath,String avatarName,boolean compareEliminateAvatar) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                status = editProfileModel.updateProfile(user, avatarPath,avatarName,compareEliminateAvatar);
                if (TextUtils.isEmpty(status) || status.equals("failure") || status.equals("avatarFailure")) {
                    mMyHandle.sendEmptyMessage(-1);
                } else if (status.equals("success")) {
                    mMyHandle.sendEmptyMessage(0);
                }
            }
        }).start();
    }
}
