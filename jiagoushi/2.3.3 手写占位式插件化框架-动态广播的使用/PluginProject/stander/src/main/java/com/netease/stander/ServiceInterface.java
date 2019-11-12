package com.netease.stander;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

public interface ServiceInterface {

    /**
     * 把宿主(app)的环境  给  插件
     * @param appService
     */
    void insertAppContext(Service appService);

    public void onCreate();

    public int onStartCommand(Intent intent, int flags, int startId);

    public void onDestroy();

}
