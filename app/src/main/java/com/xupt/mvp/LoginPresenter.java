package com.xupt.mvp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xupt.bean.User;

import java.util.Objects;

class LoginPresenter<V extends LoginContract.View> extends LoginContract.Present<V>{

    private ILoginImpl loginModel = new ILoginImpl();

    private User user;

    private Handler mMyHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Objects.requireNonNull(getMView()).hideLoading();
            if (msg.what == -2) {
                getMView().loginFailure();
            } else if (msg.what == -1) {
                getMView().wrongAccountOrPassword();
            } else if (msg.what == 0) {
                getMView().loginSuccess(user);
            }
        }
    };

    @Override
    void login(final String account, final String password) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                user = loginModel.login(account, password);
                if (user == null || user.getId() == -2) {
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