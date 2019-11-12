package com.netease.butterknife.library;

/**
 * 绑定接口类（所有注解处理器生成的类，需要实现该接口）
 *
 * @param <T> 被绑定者的类型，如：MainActivity
 */
public interface ViewBinder<T> {

    /**
     * 绑定方法
     *
     * @param target 被绑定者，如：MainActivity
     */
    void bind(T target);
}
