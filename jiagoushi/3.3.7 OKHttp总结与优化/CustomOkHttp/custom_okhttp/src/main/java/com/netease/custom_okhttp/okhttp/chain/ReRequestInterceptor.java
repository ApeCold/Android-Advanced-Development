package com.netease.custom_okhttp.okhttp.chain;

import android.util.Log;

import com.netease.custom_okhttp.okhttp.OkHttpClient2;
import com.netease.custom_okhttp.okhttp.RealCall2;
import com.netease.custom_okhttp.okhttp.Response2;

import java.io.IOException;

/**
 * 重试拦截器
 */
public class ReRequestInterceptor implements Interceptor2 {

    private final String TAG = ReRequestInterceptor.class.getSimpleName();

    @Override
    public Response2 doNext(Chain2 chain2) throws IOException {

        // Log.d(TAG, "我是重试拦截器，执行了");
        System.out.println("我是重试拦截器，执行了");

        ChainManager chainManager = (ChainManager) chain2;

        RealCall2 realCall2 = chainManager.getCall();
        OkHttpClient2 client2 = realCall2.getOkHttpClient2();

        IOException ioException = null;

        // 重试次数
        if (client2.getRecount() != 0) {
            for (int i = 0; i < client2.getRecount(); i++) { // 3
                try {
                    // Log.d(TAG, "我是重试拦截器，我要Return Response2了");
                    System.out.println("我是重试拦截器，我要Return Response2了");
                    // 如果没有异常，循环就结束了
                    Response2 response2 = chain2.getResponse(chainManager.getRequest()); // 执行下一个拦截器（任务节点）
                    return response2;
                } catch (IOException e) {
                    // e.printStackTrace();
                    ioException = e;
                }

            }
        }
        // return null;
        throw ioException;
    }
}
