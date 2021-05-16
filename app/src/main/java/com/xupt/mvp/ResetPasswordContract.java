package com.xupt.mvp;

import com.xupt.base.BasePresenter;
import com.xupt.base.IModel;
import com.xupt.base.IView;

class ResetPasswordContract {
    interface View extends IView {
        void resetSuccess();

        void wrongEmail();

        void resetFailure();
    }

    interface Model extends IModel {
        String resetPassword(String email, String newPassword);
    }

    abstract static class Present<V extends IView> extends BasePresenter<V> {
        abstract void resetPassword(String email,String newPassword);
    }
}
