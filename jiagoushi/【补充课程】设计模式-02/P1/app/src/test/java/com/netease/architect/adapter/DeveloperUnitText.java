package com.netease.architect.adapter;

import com.netease.architect.adapter.library.Retrofit;

import org.junit.Test;

public class DeveloperUnitText {

    @Test
    public void retrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(new RxJavaCallAdapterFactory())
                .build();

        retrofit.create();
    }
}
