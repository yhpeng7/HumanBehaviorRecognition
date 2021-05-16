package com.xupt.mvp;

import com.xupt.base.BasePresenter;
import com.xupt.base.IModel;
import com.xupt.base.IView;
import com.xupt.bean.User;

class EditProfileContract {
    interface View extends IView {
        void updateSuccess();

        void updateFailure(String type);
    }

    interface Model extends IModel {
        String updateProfile(String user, String avatarPath,String avatarName,boolean compareEliminateAvatar);
    }

    abstract static class Present<V extends IView> extends BasePresenter<V> {
        abstract void updateProfile(String user,String avatarPath,String avatarName,boolean compareEliminateAvatar);
    }
}
