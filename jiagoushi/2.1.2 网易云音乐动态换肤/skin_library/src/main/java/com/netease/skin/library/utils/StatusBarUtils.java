package com.netease.skin.library.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class StatusBarUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void forStatusBar(Activity activity) {
        TypedArray a = activity.getTheme().obtainStyledAttributes(0, new int[] {
                android.R.attr.statusBarColor
        });
        int color = a.getColor(0, 0);
        activity.getWindow().setStatusBarColor(color);
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void forStatusBar(Activity activity, int skinColor) {
        activity.getWindow().setStatusBarColor(skinColor);
    }
}
