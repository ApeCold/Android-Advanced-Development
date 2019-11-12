package com.netease.study_run.annotation_common;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 长按的注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnBaseCommon(setCommonListener = "setOnLongClickListener",
              setCommonObjectListener = View.OnLongClickListener.class,
              callbackMethod = "onLongClick")
public @interface OnClickLongCommon {

    int value();

}
