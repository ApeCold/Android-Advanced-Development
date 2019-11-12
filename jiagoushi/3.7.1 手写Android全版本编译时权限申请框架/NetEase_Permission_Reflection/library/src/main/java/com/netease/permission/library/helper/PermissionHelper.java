package com.netease.permission.library.helper;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * 抽象辅助类
 */
public abstract class PermissionHelper {

    private Activity activity;

    PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public static PermissionHelper newInstance(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionHelper(activity);
        }
        return new ActivityPermissionHelper(activity);
    }

    Activity getHost() {
        return activity;
    }

    /**
     * 用户申请权限
     *
     * @param requestCode    请求标识码（必须<256）
     * @param perms          需要授权的一组权限
     */
    public abstract void requestPermissions(int requestCode, String... perms);

    /**
     * 第一次打开App时    false
     * 上次弹出权限请求点击了拒绝，但没有勾选“不再询问”    true
     * 上次弹出权限请求点击了拒绝，并且勾选了“不再询问”    false
     *
     * @param deniedPermission 被拒绝的权限组
     * @return 点击了拒绝，但没有勾选“不再询问”返回ture，点击了拒绝，并且勾选了“不再询问”返回false
     */
    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String deniedPermission);

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     *
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true，反之false
     */
    public boolean somePermissionPermanentlyDenied(@NonNull List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!shouldShowRequestPermissionRationale(deniedPermission)) {
                return true;
            }
        }
        return false;
    }

}
