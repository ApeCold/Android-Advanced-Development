package com.netease.pluginproject;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.netease.stander.ServiceInterface;

public class ProxyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String className = intent.getStringExtra("className");

        // com.netease.plugin_package.TestService

        try {
            Class mTestServiceClass = PluginManager.getInstance(this).getClassLoader().loadClass(className);
            Object mTestService = mTestServiceClass.newInstance();

            ServiceInterface serviceInterface = (ServiceInterface) mTestService;

            // 注入 组件环境
            serviceInterface.insertAppContext(this);

            serviceInterface.onStartCommand(intent, flags, startId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
