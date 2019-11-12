package com.netease.study_run.annotation_common;


import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 点击的注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnBaseCommon(setCommonListener = "setOnClickListener",
              setCommonObjectListener = View.OnClickListener.class,
              callbackMethod = "onClick") // 使用作用在注解之上的 注解
public @interface OnClickCommon {

    int value();

}
