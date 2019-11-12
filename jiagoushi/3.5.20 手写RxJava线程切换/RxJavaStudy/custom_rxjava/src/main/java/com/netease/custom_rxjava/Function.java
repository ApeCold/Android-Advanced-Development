package com.netease.custom_rxjava;

public interface Function<T, R> {

    public R apply(T t); // 变换的行为标准

}
