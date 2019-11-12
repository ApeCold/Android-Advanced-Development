package com.netease.fastjson.model;

public class UserInfo {

    private String uId; // 用户ID
    private String nickName; // 昵称
    private String realName; // 真名
    private String phoneNumber; // 手机号码
    private String headerImage; // 头像
    private String email; // 邮箱
    private String address; // 地址
    private int userType; // 用户类型

    public UserInfo() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uId='" + uId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", realName='" + realName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", headerImage='" + headerImage + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", userType=" + userType +
                '}';
    }
}
