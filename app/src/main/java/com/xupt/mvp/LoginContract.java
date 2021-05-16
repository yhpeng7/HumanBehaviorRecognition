package com.xupt.mvp;

import com.xupt.base.BasePresenter;
import com.xupt.base.IModel;
import com.xupt.base.IView;
import com.xupt.bean.User;

class LoginContract {
    interface View extends IView {
        void loginSuccess(User user);

        void wrongAccountOrPassword();

        void loginFailure();
    }

    interface Model extends IModel {
        User login(String account,String password);
    }

    abstract static class Present<V extends IView> extends BasePresenter<V>{
        abstract void login(String account,String password);
    }
}
