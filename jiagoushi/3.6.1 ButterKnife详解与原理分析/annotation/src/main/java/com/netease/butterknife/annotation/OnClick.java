package com.netease.butterknife.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 注解作用在方法之上
@Retention(RetentionPolicy.CLASS) // 编译期原理（交予注解处理器）
public @interface OnClick {

    // 此处省略了int[]
    int value();
}