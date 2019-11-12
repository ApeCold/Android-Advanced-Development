package com.netease.customokhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * OKHTTP的使用
     */
    private static void okhttpUseAction() {
        Dispatcher myDispatcher = new Dispatcher();

        // 构建者模式 --》 OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder().dispatcher(myDispatcher).addInterceptor(null).build();

        // // 构建者模式 --》 Request
        // GET请求
        final Request request = new Request.Builder().url("https://www.baidu.com").get().build();

        // Call call == RealCall
        Call call = okHttpClient.newCall(request);

        // call.cancel(); // 取消请求

        // 同步方法-- 我们需要自己开启子线程  -- 耗时
//        try {
//            Response response = call.execute();
//
//            String string = response.body().string();
//            response.body().byteStream();
//            response.body().charStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 异步方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败.. E:" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String string = response.body().string();
                System.out.println("请求完成.. result:" + string);

                // 发生了异常 ...

//                response.body().byteStream();
//                response.body().charStream();

            }
        });

//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // 失败
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // 发生了异常 ...
//            }
//        });
    }

    public static void main(String[] args) {
        okhttpUseAction();
    }
}
