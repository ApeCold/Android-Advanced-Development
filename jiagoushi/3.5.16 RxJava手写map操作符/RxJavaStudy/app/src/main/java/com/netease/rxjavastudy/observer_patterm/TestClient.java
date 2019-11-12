package com.netease.rxjavastudy.observer_patterm;

// todo 这是第一个节课 观察者模式 的代码
public class TestClient {

    public static void main(String[] args) {

        Observer observer_1 = new ObserverImpl(); // 警察1  - 观察者
        Observer observer_2 = new ObserverImpl(); // 警察2  - 观察者
        Observer observer_3 = new ObserverImpl(); // 警察3  - 观察者
        Observer observer_4 = new ObserverImpl(); // 警察4  - 观察者
        Observer observer_5 = new ObserverImpl(); // 警察5  - 观察者

        // 一个小偷  被观察者
        Observable observable = new ObservableImpl();

        // 关联 注册
        observable.registerObserver(observer_1);
        observable.registerObserver(observer_2);
        observable.registerObserver(observer_3);
        observable.registerObserver(observer_4);
        observable.registerObserver(observer_5);

        // 小偷发生改变(被观察者)
        observable.notifyObservers();
    }

}
