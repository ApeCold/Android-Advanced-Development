package com.netease.permission.library.helper;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * 低版本申请权限辅助类
 */
class LowApiPermissionHelper extends PermissionHelper {

    LowApiPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(int requestCode, String... perms) {
        throw new IllegalStateException("低于6.0版本无须运行时请求权限");
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String deniedPermission) {
        return false;
    }
}
