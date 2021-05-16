package com.xupt.mvp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xupt.bean.User;

import java.util.Objects;

class RegisterPresenter<V extends RegisterContract.View> extends RegisterContract.Present<V>{

    private IRegisterImpl registerModel = new IRegisterImpl();
    private User user;

    private Handler mMyHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Objects.requireNonNull(getMView()).hideLoading();
            if (msg.what == -3) {
                getMView().registerFailure();
            } else if (msg.what == -2) {
                getMView().duplicateEmail(user.getUsername());
            } else if (msg.what == -1) {
                getMView().duplicateUsername();
            } else if (msg.what == 0) {
                getMView().registerSuccess(user);
            }
        }
    };

    @Override
    void register(final String username, final String password, final String email) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                user = registerModel.register(username, password, email);
                if (user == null || user.getId() == -3) {
                    mMyHandle.sendEmptyMessage(-3);
                } else if (user.getId() == -2) {
                    mMyHandle.sendEmptyMessage(-2);
                } else if (user.getId() == -1) {
                    mMyHandle.sendEmptyMessage(-1);
                } else {
                    mMyHandle.sendEmptyMessage(0);
                }
            }
        }).start();
    }
}
