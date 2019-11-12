package com.netease.custom_dagger2;

public interface MembersInjctor<T> { // T == 我们要把对象注入到  MainActivity那里

    void injectMembers(T instance);

}
