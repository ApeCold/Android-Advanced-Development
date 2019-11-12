package com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp;

import io.reactivex.Observable;
import retrofit2.http.Body;

public interface IRequestNetwork {

    // 请求注册 功能  todo 耗时操作 ---> OkHttp
    public Observable<RegisterResponse> registerAction(@Body RegisterRequest registerRequest);

    // 请求登录 功能 todo 耗时操作 ---> OKHttp
    public Observable<LoginResponse> loginAction(@Body LoginRequest loginRequest);
}
