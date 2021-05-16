package com.xupt.mvp;

import com.xupt.base.BasePresenter;
import com.xupt.base.IModel;
import com.xupt.base.IView;

public class MainContract {
    interface View extends IView {
        void cancelSuccess();

        void cancelFailure();
    }

    interface Model extends IModel {
        String cancelAccount(String id);
    }

    abstract static class Present<V extends IView> extends BasePresenter<V>{
        abstract void cancelAccount(String id);
    }
}
