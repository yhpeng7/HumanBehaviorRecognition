package com.xupt.bean;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String avatar;
    private String birthday;
    private int gender;
    private String region;
    private String username;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", avatar:'" + avatar + '\'' +
                ", birthday:'" + birthday + '\'' +
                ", gender:" + gender +
                ", region:'" + region + '\'' +
                ", username:'" + username + '\'' +
                ", email:'" + email + '\'' +
                '}';
    }
}
