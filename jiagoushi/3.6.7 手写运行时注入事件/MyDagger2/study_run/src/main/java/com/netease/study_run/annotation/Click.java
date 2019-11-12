package com.netease.study_run.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 作用域在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时期
public @interface Click {

    int value();

}
