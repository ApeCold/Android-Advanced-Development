package com.netease.eventbus.demo;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

/**
 * http://greenrobot.org/eventbus/documentation/subscriber-index/
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
