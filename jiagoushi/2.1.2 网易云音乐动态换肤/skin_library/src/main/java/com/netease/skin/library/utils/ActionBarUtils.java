package com.netease.skin.library.utils;

import android.annotation.TargetApi;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class ActionBarUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void forActionBar(AppCompatActivity activity) {
        TypedArray a = activity.getTheme().obtainStyledAttributes(0, new int[] {
                android.R.attr.colorPrimary
        });
        int color = a.getColor(0, 0);
        a.recycle();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(color));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void forActionBar(AppCompatActivity activity, int skinColor) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(skinColor));
        }
    }
}
