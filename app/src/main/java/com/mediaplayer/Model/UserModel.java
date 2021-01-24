package com.mediaplayer.Model;

public class UserModel {
    String pictureUrl;
    String name;
    String mobile;
    String email;

    public UserModel() {
    }

    public UserModel(String pictureUrl, String name, String mobile, String email) {
        this.pictureUrl = pictureUrl;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
