package com.netease.custom_rxjava;

// todo 被观察者 上游
public class Observable<T> { // 类声明的泛型T  Int

    ObservableOnSubscribe<T> source;

    private Observable(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    // 静态方法声明的<T>泛型        ObservableOnSubscribe<T>==静态方法声明的<T>泛型
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) { // int
        return new Observable<T>(source); // 静态方法声明的<T>泛型 int
    }

    // new Observable<T>(source).subscribe(Observer<Int>)
    public void subscribe(Observer<T> observer) {

    }
}
