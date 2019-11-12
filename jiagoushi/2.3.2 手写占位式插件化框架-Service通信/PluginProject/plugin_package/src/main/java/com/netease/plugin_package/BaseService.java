package com.netease.plugin_package;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.netease.stander.ServiceInterface;

public class BaseService extends Service implements ServiceInterface {

    public Service appService;

    /**
     * 把宿主(app)的环境  给  插件
     * @param appService
     */
    public void insertAppContext(Service appService){
        this.appService = appService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    @Override
    public void onDestroy() {

    }
}
