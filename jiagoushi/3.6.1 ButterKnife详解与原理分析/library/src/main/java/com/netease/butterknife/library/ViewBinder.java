package com.netease.butterknife.library;

/**
 * 接口绑定类（所有注解处理器生的类，都需要实现该接口，= 接口实现类）
 */
public interface ViewBinder<T> {

    void bind(T target);
}
