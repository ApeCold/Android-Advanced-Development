package com.netease.custom_rxjava;

// todo 观察者 下游
public interface Observer<T> { // Int

    public void onSubscribe();

    public void onNext(T item);  // Int

    public void onError(Throwable e);

    public void onComplete();

}
