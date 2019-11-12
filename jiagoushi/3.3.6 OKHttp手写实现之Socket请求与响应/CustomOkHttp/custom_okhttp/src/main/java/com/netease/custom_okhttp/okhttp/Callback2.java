package com.netease.custom_okhttp.okhttp;

import java.io.IOException;

public interface Callback2 {

    void onFailure(Call2 call, IOException e);

    void onResponse(Call2 call, Response2 response) throws IOException;
}
