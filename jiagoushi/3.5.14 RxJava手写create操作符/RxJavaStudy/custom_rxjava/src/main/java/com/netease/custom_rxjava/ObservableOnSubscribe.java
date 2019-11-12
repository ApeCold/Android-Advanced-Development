package com.netease.custom_rxjava;

import android.arch.lifecycle.Observer;

public interface ObservableOnSubscribe<T> { // T == String

    public void subscribe(Observer<T> observableEmitter); // Observer<String>

}
