package com.netease.pluginhookandroid9;

import android.os.Build;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 对于系统版本的判断
 */
public class AndroidSdkVersion {

    /**
     * TODO 本来用枚举是最好的，这里干脆就这样写了，时间上来不及了
     * API Level 21 --- Android 5.0
     * API Level 22 --- Android 5.1
     * API Level 23 --- Android 6.0
     * API Level 24 --- Android 7.0
     * API Level 25 --- Android 7.1.1
     * API Level 26 --- Android 8.0
     * API Level 27 --- Android 8.1
     * API Level 28 --- Android 9.0
     */

    /**
     * TODO API Level 26 --- Android 8.0
     * TODO API Level 27 --- Android 8.1
     * TODO API Level 28 --- Android 9.0
     */
    /**
     * 判断当前系统版本 26 27 28
     * @return
     */
    public static boolean isAndroidOS_26_27_28() {
        int V = Build.VERSION.SDK_INT;
        if ((V > 26 || V == 26) && (V < 28 || V == 28)) {
            return true;
        }
        return false;
    }

    /**
     * TODO API Level 21 --- Android 5.0
     * TODO API Level 22 --- Android 5.1
     * TODO API Level 23 --- Android 6.0
     * TODO API Level 24 --- Android 7.0
     * TODO API Level 25 --- Android 7.1.1
     */
    /**
     * 判断当前系统版本 21 22 23 24 25 以及 21版本之下的
     * @return
     */
    public static boolean isAndroidOS_21_22_23_24_25() {
        int V = Build.VERSION.SDK_INT;
        if (V < 26) {
            return true;
        }
        return false;
    }

}
