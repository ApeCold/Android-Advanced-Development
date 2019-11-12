package com.netease.dagger2;

import android.app.Activity;

import dagger.Component;

@Component(modules = StudentModule.class) // 快递员拿到了包裹
public interface StudentComponent {

    // 送到收货地址 --- 注入到Activity
    void injectMainActivity(MainActivity mainActivity); // 不支持多态功能的

    void injectMainActivity(MainActivity2 mainActivity);

    void injectMainActivity(MainActivity3 mainActivity);

    void injectMainActivity(MainActivity4 mainActivity);

}
