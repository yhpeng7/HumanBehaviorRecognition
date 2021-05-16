package com.xupt.mvp;

import com.xupt.base.BasePresenter;
import com.xupt.base.IModel;
import com.xupt.base.IView;
import com.xupt.bean.User;

class RegisterContract {
    interface View extends IView{
        void registerSuccess(User user);

        void registerFailure();

        void duplicateUsername();

        void duplicateEmail(String username);
    }

    interface Model extends IModel{
        User register(String username, String password, String email);
    }

    abstract static class Present<V extends IView> extends BasePresenter<V> {
        abstract void register(String username,String password,String email);
    }
}
