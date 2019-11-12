package com.netease.premissionstudy.permission.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.collection.SimpleArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.netease.premissionstudy.permission.menu.DefaultStartSettins;
import com.netease.premissionstudy.permission.menu.IMenu;
import com.netease.premissionstudy.permission.menu.OPPOStartSettings;
import com.netease.premissionstudy.permission.menu.VIVOStartSettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    // 定义八种权限
    private static SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(8);
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23);
    }


    // todo //////////////////////////////////////////////////////////////////////////////////////////


    private static HashMap<String, Class<? extends IMenu>> permissionMenu = new HashMap<>();

    private static final String MANUFACTURER_DEFAULT = "Default";//默认

    public static final String MANUFACTURER_HUAWEI = "huawei";//华为
    public static final String MANUFACTURER_MEIZU = "meizu";//魅族
    public static final String MANUFACTURER_XIAOMI = "xiaomi";//小米
    public static final String MANUFACTURER_SONY = "sony";//索尼
    public static final String MANUFACTURER_OPPO = "oppo";
    public static final String MANUFACTURER_LG = "lg";
    public static final String MANUFACTURER_VIVO = "vivo";
    public static final String MANUFACTURER_SAMSUNG = "samsung";//三星
    public static final String MANUFACTURER_LETV = "letv";//乐视
    public static final String MANUFACTURER_ZTE = "zte";//中兴
    public static final String MANUFACTURER_YULONG = "yulong";//酷派
    public static final String MANUFACTURER_LENOVO = "lenovo";//联想

    static {
        permissionMenu.put(MANUFACTURER_DEFAULT, DefaultStartSettins.class);
        permissionMenu.put(MANUFACTURER_OPPO, OPPOStartSettings.class);
        permissionMenu.put(MANUFACTURER_VIVO, VIVOStartSettings.class);
    }

    /**
     * TODO 检查是否需要去请求权限，此方法目的：就是检查 是否已经授权了
     *
     * @param context
     * @param permissions
     * @return 返回false代表需要请求权限，  返回true代表不需要请求权限 就可以结束MyPermisisonActivity了
     */
    public static boolean hasPermissionRequest(Context context, String... permissions) {
        for (String permission : permissions) {
            if (permissionExists(permission) && isPermissionReqeust(context, permission) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前SDK 权限是否存在 如果存在就return true
     *
     * @param permission
     * @return
     */
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        return minVersion == null || minVersion <= Build.VERSION.SDK_INT;
    }

    /**
     * 判断参数中传递进去的权限是否已经被授权了
     *
     * @param context
     * @param permission
     * @return
     */
    private static boolean isPermissionReqeust(Context context, String permission) {
        try {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    // TODO 最后判断下 是否真正的成功
    public static boolean requestPermissionSuccess(int... gantedResult) {
        if (gantedResult == null || gantedResult.length <= 0) {
            return false;
        }

        for (int permissionValue : gantedResult) {
            if (permissionValue != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // TODO 说白了：就是用户被拒绝过一次，然后又弹出这个框，【需要给用户一个解释，为什么要授权，就需要执行此方法判断】
    // 当用户点击了不再提示，这种情况要考虑到才行
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    // TODO 专门去 callback invoke ---》 MainActivity  被注解的方法
    public static void invokeAnnotation(Object object, Class annotationClass) {
        // 获取 object 的 Class对象
        Class<?> objectClass = object.getClass();

        // 遍历 所有的方法
        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true); // 让虚拟机，不要去检测 private

            // 判断方法 是否有被 annotationClass注解的方法
            boolean annotationPresent = method.isAnnotationPresent(annotationClass);

            if (annotationPresent) {
                // 当前方法 代表包含了 annotationClass注解的
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO 专门去 跳转到 设置界面
    public static void startAndroidSettings(Context context) {

        // 拿到当前手机品牌制造商，来获取 具体细节

        Class aClass = permissionMenu.get(Build.MANUFACTURER.toLowerCase());

        if (aClass == null) {
            aClass = permissionMenu.get(MANUFACTURER_DEFAULT);
        }

        try {
            Object newInstance = aClass.newInstance(); // new OPPOStartSettings()

            IMenu iMenu = (IMenu) newInstance; // IMenu iMenu = (IMenu) oPPOStartSettings;

            // 高层 面向抽象，而不是具体细节
            Intent startActivityIntent = iMenu.getStartActivity(context);

            if (startActivityIntent != null) {
                context.startActivity(startActivityIntent);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
