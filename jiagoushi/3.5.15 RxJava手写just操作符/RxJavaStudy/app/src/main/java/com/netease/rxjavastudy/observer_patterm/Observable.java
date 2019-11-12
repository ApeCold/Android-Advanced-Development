package com.netease.rxjavastudy.observer_patterm;

// 被观察者标准
public interface Observable {

    /**
     * 在被观察者中  注册  观察者
     */
    void registerObserver(Observer observer);

    /**
     * 在被观察者中 移除 观察者
     */
    void removeObserver(Observer observer);

    /**
     * 在被观察者中 通知 所有注册的 观察者
     */
    void notifyObservers();

}
