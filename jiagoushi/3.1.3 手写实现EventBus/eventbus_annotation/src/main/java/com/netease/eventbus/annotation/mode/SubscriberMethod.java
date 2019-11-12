package com.netease.eventbus.annotation.mode;

import java.lang.reflect.Method;

/**
 * 事件订阅方法封装类
 */
public class SubscriberMethod {

    private String methodName; // 订阅方法名
    private Method method; // 订阅方法，用于最后的自动执行订阅方法
    private ThreadMode threadMode; // 线程模式
    private Class<?> eventType; // 事件对象Class，如：UserInfo.class
    private int priority; // 事件订阅优先级（实现思路：重排序集合中方法的顺序）
    private boolean sticky; // 是否粘性事件（实现思路：发送时存储，注册时判断粘性再激活）

    public SubscriberMethod(Class subscriberClass, String methodName,
                            Class<?> eventType, ThreadMode threadMode, int priority, boolean sticky) {
        this.methodName = methodName;
        this.threadMode = threadMode;
        this.eventType = eventType;
        this.priority = priority;
        this.sticky = sticky;
        try {
            // 订阅所属类（参考源码：AbstractSubscriberInfo.java - 72行）
            method = subscriberClass.getDeclaredMethod(methodName, eventType);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public String getMethodName() {
        return methodName;
    }

    public Method getMethod() {
        return method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isSticky() {
        return sticky;
    }
}
