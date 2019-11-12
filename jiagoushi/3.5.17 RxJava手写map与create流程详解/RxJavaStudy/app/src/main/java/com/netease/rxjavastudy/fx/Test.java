package com.netease.rxjavastudy.fx;

public class Test<T> {

    private T t;

    public void add(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

}
