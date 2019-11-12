package com.netease.permission.library.helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * 符合运行时加载权限辅助类
 */
class ActivityPermissionHelper extends PermissionHelper {

    ActivityPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(int requestCode, String... perms) {
        ActivityCompat.requestPermissions(getHost(), perms, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String deniedPermission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), deniedPermission);
    }
}
