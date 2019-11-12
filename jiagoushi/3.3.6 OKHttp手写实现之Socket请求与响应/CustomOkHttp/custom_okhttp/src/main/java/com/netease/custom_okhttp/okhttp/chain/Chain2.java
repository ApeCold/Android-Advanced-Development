package com.netease.custom_okhttp.okhttp.chain;

import com.netease.custom_okhttp.okhttp.Request2;
import com.netease.custom_okhttp.okhttp.Response2;

import java.io.IOException;

public interface Chain2 {

    Request2 getRequest();

    Response2 getResponse(Request2 request2) throws IOException;

}
