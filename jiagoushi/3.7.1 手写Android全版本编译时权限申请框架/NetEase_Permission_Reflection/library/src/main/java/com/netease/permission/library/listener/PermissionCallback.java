package com.netease.permission.library.listener;

import java.util.List;

/**
 * 权限回调监听
 */
public interface PermissionCallback {

    /**
     * 授予授权通过返回
     * @param requestCode 权限请求标识码
     * @param perms 请求的权限组（方便debug，如用户只授权权限组部分权限）
     */
    void onPermissionGranted(int requestCode, List<String> perms);

    /**
     * 权限被拒绝返回
     * @param requestCode 权限请求标识码
     * @param perms 请求的权限组
     */
    void onPermissionDenied(int requestCode, List<String> perms);
}
