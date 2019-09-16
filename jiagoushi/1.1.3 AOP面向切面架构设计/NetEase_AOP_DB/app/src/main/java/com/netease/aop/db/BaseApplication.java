package com.netease.aop.db;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 谷歌工程师做了AOP的思想
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("netease >>> ", activity.getComponentName().getClassName() + " Created");
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("netease >>> ", activity.getComponentName().getClassName() + " Destroyed");
            }
        });
    }
}
