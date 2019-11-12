package com.netease.custom_okhttp.okhttp.chain;

import com.netease.custom_okhttp.okhttp.Response2;

import java.io.IOException;

public interface Interceptor2 {

    Response2 doNext(Chain2 chain2) throws IOException;

}
