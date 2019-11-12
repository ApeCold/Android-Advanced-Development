package com.netease.eventbus.annotation;

import com.netease.eventbus.annotation.mode.SubscriberInfo;

/**
 * 所有的事件订阅方法，生成索引接口
 */
public interface SubscriberInfoIndex {

    /**
     * 生成索引接口，通过订阅者对象（MainActivity.class）获取所有订阅方法
     *
     * @param subscriberClass 订阅者对象Class，如：MainActivity.class
     * @return 事件订阅方法封装类
     */
    SubscriberInfo getSubscriberInfo(Class<?> subscriberClass);
}
