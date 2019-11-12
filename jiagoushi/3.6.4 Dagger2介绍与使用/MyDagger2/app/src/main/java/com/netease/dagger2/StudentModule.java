package com.netease.dagger2;

import dagger.Module;
import dagger.Provides;

// 耳机的包裹
@Module
public class StudentModule {

    @Provides
    public Student getStudent() {
        return new Student(99);
    }

}
