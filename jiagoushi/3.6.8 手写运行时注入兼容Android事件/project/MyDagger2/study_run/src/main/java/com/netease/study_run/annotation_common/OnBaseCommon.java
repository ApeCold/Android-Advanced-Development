package com.netease.study_run.annotation_common;

// 专门处理事件三要素的 注解

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE) // 本身自己就是注解，还可以作用域在 注解之上
@Retention(RetentionPolicy.RUNTIME) // 运行时期
public @interface OnBaseCommon {

    // todo 事件三要素1 订阅方式  setOnClickListener， setOnLongClickListener  ...
    String setCommonListener();

    // todo 事件三要素2 事件源对象 View.OnClickListener，  View.OnLongClickListener  ...
    Class setCommonObjectListener();

    // todo 事件三要素3 具体执行的方法（消费事件的方法）   onClick(View v) ，  onLongClick(View v)
    String callbackMethod();
}
