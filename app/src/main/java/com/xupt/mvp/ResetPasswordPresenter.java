package com.xupt.mvp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.Objects;

public class ResetPasswordPresenter<V extends ResetPasswordContract.View> extends ResetPasswordContract.Present<V>{

    private IResetPasswordImpl resetPasswordModel = new IResetPasswordImpl();
    private String status;

    private Handler mMyHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Objects.requireNonNull(getMView()).hideLoading();
            if (msg.what == -2) {
                getMView().resetFailure();
            } else if (msg.what == 0) {
                getMView().resetSuccess();
            } else if (msg.what == -1) {
                getMView().wrongEmail();
            }
        }
    };

    @Override
    void resetPassword(final String email, final String newPassword) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                status = resetPasswordModel.resetPassword(email, newPassword);
                if (status == null) {
                    mMyHandle.sendEmptyMessage(-2);
                } else if (status.equals("success")) {
                    mMyHandle.sendEmptyMessage(-0);
                } else if (status.equals("failure")) {
                    mMyHandle.sendEmptyMessage(-1);
                }
            }
        }).start();
    }
}
