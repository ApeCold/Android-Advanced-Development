package com.netease.skin.library.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Build;

public class NavigationUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void forNavigation(Activity activity) {
        TypedArray a = activity.getTheme().obtainStyledAttributes(0, new int[] {
                android.R.attr.statusBarColor
        });
        int color = a.getColor(0, 0);
        activity.getWindow().setNavigationBarColor(color);
        a.recycle();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void forNavigation(Activity activity, int skinColor) {
        activity.getWindow().setNavigationBarColor(skinColor);
    }
}
