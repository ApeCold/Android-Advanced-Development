package com.netease.hotfix.demo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.netease.hotfix.library.FixDexUtils;

public class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        // 加载热修复Dex文件
        FixDexUtils.loadFixedDex(context);
    }
}
