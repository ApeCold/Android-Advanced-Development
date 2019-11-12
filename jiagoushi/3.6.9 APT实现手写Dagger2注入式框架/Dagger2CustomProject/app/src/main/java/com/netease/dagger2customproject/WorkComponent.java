package com.netease.dagger2customproject;

import javax.inject.Singleton;

import dagger.Component;

@Singleton // 只在注入的MainActivity中是单例 如何局部单例（DoubleCheck）
@Component(modules = WorkModule.class)
public interface WorkComponent {

    void inject(MainActivity mainActivity);

}
