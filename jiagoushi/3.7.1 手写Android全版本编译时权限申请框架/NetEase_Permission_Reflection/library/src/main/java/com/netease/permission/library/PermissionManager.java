package com.netease.permission.library;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.netease.permission.library.annotation.IPermission;
import com.netease.permission.library.helper.PermissionHelper;
import com.netease.permission.library.listener.PermissionCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    /**
     * 检查所请求的权限是否被授予
     *
     * @param activity 当前Activity
     * @param perms    一个或多个请求的权限
     * @return 如果所有的权限都已被授予返回true，反之哪怕有一个没有被授予通过返回false
     */
    public static boolean hasPermissions(Activity activity, @NonNull String... perms) {
        if (activity == null) {
            throw new IllegalArgumentException("不能传入一个空的Activity");
        }

        // 如果低于6.0版本无须做运行时权限判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            // 如果循环出任意一个权限没有被授予则返回false
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 向用户申请权限
     *
     * @param activity    当前Activity
     * @param requestCode 请求标识码（必须<256）
     * @param perms       需要授权的一组权限
     */
    public static void requestPermissions(@NonNull Activity activity,
                                          int requestCode,
                                          @NonNull String... perms) {
        // 发起权限请求前检查权限状态
        if (hasPermissions(activity, perms)) { // 全部通过
            notifyHasPermissions(activity, requestCode, perms);
            return;
        }
        // 权限请求
        PermissionHelper helper = PermissionHelper.newInstance(activity);
        helper.requestPermissions(requestCode, perms);
    }

    /**
     * 如果全部已被授权则进入onRequestPermissionResult方法返回结果
     *
     * @param activity    当前Activity
     * @param requestCode 请求标识码
     * @param perms       授权通过的权限
     */
    private static void notifyHasPermissions(Activity activity, int requestCode, String[] perms) {
        // 二次检查，将授权通过的权限组转参告知处理权限结果方法
        int[] grantResults = new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        onRequestPermissionResult(requestCode, perms, grantResults, activity);
    }

    /**
     * 处理权限请求结果方法
     * 如果授予或者拒绝任何权限，将通过PermissionCallback回调接收结果
     * 以及运行有@IPermission注解的方法
     *
     * @param requestCode  回调请求标识码
     * @param permissions  回调权限组
     * @param grantResults 回调授权结果
     * @param activity     拥有实现PermissionCallback接口或者有@IPermission注解的Activity
     */
    public static void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        // int length = permissions.length
        for (int i = 0; i < permissions.length; i++) {
            // 遍历权限请求结果，分类加入集合
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        // 回调授权通过结果
        if (!granted.isEmpty()) {
            if (activity instanceof PermissionCallback) {
                ((PermissionCallback) activity).onPermissionGranted(requestCode, granted);
            }
        }

        // 回调授权拒绝结果
        if (!denied.isEmpty()) {
            if (activity instanceof PermissionCallback) {
                ((PermissionCallback) activity).onPermissionDenied(requestCode, denied);
            }
        }

        // 如果授权全部都通过，才执行注解方法。哪怕多个权限中一个被拒绝也不执行方法
        if (!granted.isEmpty() && denied.isEmpty()) {
            reflectAnnotationMethod(activity, requestCode);
        }
    }

    /**
     * 找到指定Activity中，有IPermission注解的，并且请求标识码参数的正确方法
     *
     * @param activity    当前Activity
     * @param requestCode 注解中的参数请求标识码
     */
    private static void reflectAnnotationMethod(Activity activity, int requestCode) {
        // 获取类
        Class<? extends Activity> clazz = activity.getClass();
        // 获取类的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        // 遍历所有方法
        for (Method method : methods) {
            // 如果方法是IPermission注解
            if (method.isAnnotationPresent(IPermission.class)) {
                // 获取注解
                IPermission iPermission = method.getAnnotation(IPermission.class);
                // 如果注解的值等于请求标识码（两次匹配，避免框架冲突）
                if (iPermission.value() == requestCode) {

                    // 严格控制方法格式和规范
                    // 方法必须是返回void（三次匹配）
                    Type returnType = method.getGenericReturnType();
                    if (!"void".equals(returnType.toString())) {
                        throw new RuntimeException(method.getName() + "方法返回必须是void");
                    }
                    // 方法参数（四次匹配）
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length > 0) {
                        throw new RuntimeException(method.getName() + "方法无参数");
                    }

                    // 如果自定义方法是私有修饰符，则设置可以访问
                    try {
                        if (!method.isAccessible()) method.setAccessible(true);
                        method.invoke(activity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”的权限
     *
     * @param activity          当前Activity
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一“不再询问”的权限返回true，反之false
     */
    public static boolean somePermissionPermanentlyDenied(Activity activity, List<String> deniedPermissions) {
        return PermissionHelper.newInstance(activity).somePermissionPermanentlyDenied(deniedPermissions);
    }
}
