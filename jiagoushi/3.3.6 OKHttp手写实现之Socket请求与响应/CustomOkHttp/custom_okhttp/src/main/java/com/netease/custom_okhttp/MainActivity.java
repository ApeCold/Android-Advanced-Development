package com.netease.custom_okhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.netease.custom_okhttp.okhttp.Call2;
import com.netease.custom_okhttp.okhttp.Callback2;
import com.netease.custom_okhttp.okhttp.OkHttpClient2;
import com.netease.custom_okhttp.okhttp.Request2;
import com.netease.custom_okhttp.okhttp.RequestBody2;
import com.netease.custom_okhttp.okhttp.Response2;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String PATH = "http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * OKHTTP
     * @param view
     */
    public void useOkhttp(View view) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        final Request request = new Request.Builder().url(PATH).build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("OKHTTP请求失败....");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("OKHTTP请求成功.... result:" + response.body().string());
            }
        });

    }

    /**
     * 使用我们自己的OKHTTP
     * @param view
     */
    public static void useMyOkhttp(View view) {
        OkHttpClient2 okHttpClient2 = new OkHttpClient2.Builder().build();

        // POST ?city=110101&key=13cb58f5884f9749287abbead9c658f2
//        RequestBody2 requestBody2 = new RequestBody2();
//        requestBody2.addBody("city", "110101");
//        requestBody2.addBody("key", "13cb58f5884f9749287abbead9c658f2");

        Request2 request2 = new Request2.Builder().get().url(PATH).build();

        Call2 call2 = okHttpClient2.newCall(request2);

        // 执行异步
        call2.enqueue(new Callback2() {
            @Override
            public void onFailure(Call2 call, IOException e) {
                System.out.println("自定义OKHTTP请求失败....");
                // 发生了异常
            }

            @Override
            public void onResponse(Call2 call, Response2 response) throws IOException {
                System.out.println("OKHTTP请求成功.... result:" + response.getBody() + " 请求结果码：" + response.getStatusCode());
            }
        });
    }


}
