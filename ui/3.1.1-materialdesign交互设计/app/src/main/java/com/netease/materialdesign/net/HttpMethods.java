package com.netease.materialdesign.net;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.netease.materialdesign.bean.Movie;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HttpMethods {
    private static final String BASE_URL = "https://api.douban.com/v2/movie/";

    private static final int DEFAULT_TIMEOUT = 10;

    private MovieService movieService;

    private HttpMethods() {

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        //设置超时时间，并且设置时间的单位
        okBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //设置okhttp的缓存的机制
                .addNetworkInterceptor(new StethoInterceptor());
//                .cache(cache);

        //进行retrofit的初始化操作
        Retrofit retrofit = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(okBuilder.build())
                .build();

        //初始化接口方法中的实例
        movieService = retrofit.create(MovieService.class);
    }


    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    public void getTopMovie(Subscriber<Movie> subscriber, int start, int count) {
        if (movieService != null) {
            movieService.getTopMovie(start, count)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    //观察者的运行的线程
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }

    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }


}
