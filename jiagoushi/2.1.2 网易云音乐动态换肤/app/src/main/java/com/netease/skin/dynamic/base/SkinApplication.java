package com.netease.skin.dynamic.base;

import android.app.Application;

import com.netease.skin.library.SkinManager;

public class SkinApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
