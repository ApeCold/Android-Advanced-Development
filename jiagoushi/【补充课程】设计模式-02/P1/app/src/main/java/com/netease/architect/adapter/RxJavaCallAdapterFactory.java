package com.netease.architect.adapter;

import com.netease.architect.adapter.library.Call;
import com.netease.architect.adapter.library.CallAdapter;

import rx.Observable;

public class RxJavaCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get() {
        return new CallAdapter<Object, Observable<?>>() {

            @Override
            public Observable<?> adapt(Call<Object> call) {

                System.out.println("Observable >>> ");
                Observable.OnSubscribe func = new Observable.OnSubscribe() {
                    @Override
                    public void call(Object o) {

                    }
                };

                return Observable.create(func);
            }
        };
    }
}
