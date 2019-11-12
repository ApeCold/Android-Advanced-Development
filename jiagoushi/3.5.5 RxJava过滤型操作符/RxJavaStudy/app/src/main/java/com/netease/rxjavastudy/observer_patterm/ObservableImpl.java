package com.netease.rxjavastudy.observer_patterm;

import java.util.ArrayList;
import java.util.List;

/**
 * 被观察者  实现类
 */
public class ObservableImpl implements Observable {

    private List<Observer> observerList = new ArrayList<>(); // 观察者容器 5

    @Override
    public void registerObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observerList) {
            // 在被观察者实现类中，通知所有注册好的观察者
            observer.changeAction("被观察者 发生了改变...");
        }
    }
}
