package com.netease.custom_rxjava;

// todo 被观察者 上游
public class Observable<T> { // 类声明的泛型T  Int

    ObservableOnSubscribe source;

    private Observable(ObservableOnSubscribe source) {
        this.source = source;
    }

    // 静态方法声明的<T>泛型        ObservableOnSubscribe<T>==静态方法声明的<T>泛型
    // 参数中：ObservableOnSubscribe<? extends T> 和可读可写模式没有任何关系，还是我们之前的那一套思想（上限和下限）
    public static <T> Observable<T> create(ObservableOnSubscribe<? extends T> source) { // int
        return new Observable<T>(source); // 静态方法声明的<T>泛型 int
    }

    // new Observable<T>(source).subscribe(Observer<Int>)
    // 参数中：Observer<? extends T> 和可读可写模式没有任何关系，还是我们之前的那一套思想（上限和下限）
    public void subscribe(Observer<? extends T> observer) {

        observer.onSubscribe();

        source.subscribe(observer);

    }
}
