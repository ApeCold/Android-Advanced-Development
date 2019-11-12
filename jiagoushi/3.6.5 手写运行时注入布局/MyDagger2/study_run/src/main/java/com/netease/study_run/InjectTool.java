package com.netease.study_run;

import android.util.Log;

import com.netease.study_run.annotation.ContentView;

import java.lang.reflect.Method;

public class InjectTool {

    private static final String TAG = InjectTool.class.getSimpleName();

    public static void inject(Object object) {
        injectSetContentView(object);
    }

    /**
     * 把布局注入到 Activity中去
     * @param object == MainActivity
     */
    private static void injectSetContentView(Object object) {

        Class<?> mMainActivityClass = object.getClass();

        // 拿到MainActivity里面的（ContentView）注解
        ContentView mContentView = mMainActivityClass.getAnnotation(ContentView.class);

        if (null == mContentView) {
            Log.d(TAG, "ContentView is null ");
            return;
        }

        // 拿到用户设置的布局ID
        int layoutID = mContentView.value();

        // 我们需要执行 setContentView(R.layout.activity_main); 把布局注入到Activity
        try {
            Method setContentViewMethod = mMainActivityClass.getMethod("setContentView", int.class);
            setContentViewMethod.invoke(object, layoutID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
