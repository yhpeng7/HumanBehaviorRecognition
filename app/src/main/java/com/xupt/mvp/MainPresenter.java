package com.xupt.mvp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Objects;

class MainPresenter<V extends MainContract.View> extends MainContract.Present<V>{

    private final IMainImpl mainModel = new IMainImpl();
    private String status;

    private final Handler mMyHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Objects.requireNonNull(getMView()).hideLoading();
            if (msg.what == -1) {
                getMView().cancelFailure();
            } else if (msg.what == 0) {
                getMView().cancelSuccess();
            }
        }
    };

    @Override
    void cancelAccount(String id) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                status = mainModel.cancelAccount(id);
                if (status == null || status.equals("failure")) {
                    mMyHandle.sendEmptyMessage(-1);
                } else if (status.equals("success")) {
                    mMyHandle.sendEmptyMessage(0);
                }
            }
        }).start();
    }
}
