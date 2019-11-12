package com.netease.butterknife.library;

import android.app.Activity;

public class ButterKnife {

    public static void bind(Activity activity) {
        String className = activity.getClass().getName() + "$ViewBinder";

        try {
            Class<?> viewBindClass = Class.forName(className);
            // 接口 = 接口实现类
            ViewBinder viewBinder = (ViewBinder) viewBindClass.newInstance();
            viewBinder.bind(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
