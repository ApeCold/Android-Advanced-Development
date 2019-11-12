package com.netease.study_run.annotation_common;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnBaseCommon(setCommonListener = "setOnDragListener",
             setCommonObjectListener = View.OnDragListener.class,
             callbackMethod = "onDrag")
public @interface OnDragCommon {

    int value();

}
