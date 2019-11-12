package com.netease.use_custom_dagger2;

import androidx.appcompat.app.AppCompatActivity;

import com.netease.custom_dagger2.ann.Component;

@Component(modules = StudentModule.class) // 第三个注解
public interface StudentComponent { // 快递

    void inject(MainActivity mainActivity); // 把Student对象注入 ---> MainActivity

    // void inject(AppCompatActivity appCompatActivity); // 不支持这种方式， 注入的对象要很明确

}
