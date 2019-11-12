package com.netease.dagger2customproject;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WorkModule {

    @Singleton // 只在注入的MainActivity中是单例 如何局部单例（DoubleCheck）
    @Provides
    public Work providerWork() {
        return new Work();
    }

}
