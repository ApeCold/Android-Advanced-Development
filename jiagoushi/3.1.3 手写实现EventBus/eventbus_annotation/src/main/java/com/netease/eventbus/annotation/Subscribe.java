package com.netease.eventbus.annotation;

import com.netease.eventbus.annotation.mode.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 该注解作用在方法之上
@Retention(RetentionPolicy.CLASS) // 要在编译时进行一些预处理操作，注解会在class文件中存在
public @interface Subscribe {

    // 线程模式，默认推荐POSTING（订阅、发布在同一线程）
    ThreadMode threadMode() default ThreadMode.POSTING;

    // 是否使用粘性事件
    boolean sticky() default false;

    // 事件订阅优先级，在同一个线程中。数值越大优先级越高。
    int priority() default 0;
}
