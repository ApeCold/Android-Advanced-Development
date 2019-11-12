package com.netease.premissionstudy.permission.core;

public interface IPermission {

    void ganted(); // 已经授权

    void cancel(); // 取消授权

    void denied(); // 拒绝授权

}
