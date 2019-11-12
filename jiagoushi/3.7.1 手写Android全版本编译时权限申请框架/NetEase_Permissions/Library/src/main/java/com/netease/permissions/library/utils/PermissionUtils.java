package com.netease.permissions.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public final class PermissionUtils {

    private PermissionUtils() {
    }

    /**
     * 检查所有权限是否已允许
     *
     * @param grantResults 授权结果
     * @return 如果所有权限已允许，则返回true
     */
    public static boolean verifyPermissions(int... grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 用户申请权限组
     *
     * @param context     上下文
     * @param permissions 权限集合
     * @return 如果所有权限已允许，则返回true
     */
    public static boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!hasSelfPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 用户申请权限
     *
     * @param context    上下文
     * @param permission 权限
     * @return 有权限返回true（6.0以下直接返回true）
     * @see #hasSelfPermissions(Context, String...)
     */
    private static boolean hasSelfPermission(Context context, String permission) {
        // 如果低于6.0版本无须做运行时权限判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        try {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException t) {
            return false;
        }
    }

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     * <p>
     * 第一次打开App时    false
     * 上次弹出权限请求点击了拒绝，但没有勾选“不再询问”    true
     * 上次弹出权限请求点击了拒绝，并且勾选了“不再询问”    false
     * 点击了拒绝，但没有勾选“不再询问”返回ture，点击了拒绝，并且勾选了“不再询问”返回false
     *
     * @param permissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true，反之false
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

}
