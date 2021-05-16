package com.xupt.event;

import com.xupt.bean.User;

public class UpdateProfileSuccessEvent {
    private User mUser;

    public UpdateProfileSuccessEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
