package com.netease.custom_okhttp.okhttp;

import com.netease.custom_okhttp.okhttp.chain.ChainManager;
import com.netease.custom_okhttp.okhttp.chain.ConnectionServerInterceptor;
import com.netease.custom_okhttp.okhttp.chain.Interceptor2;
import com.netease.custom_okhttp.okhttp.chain.ReRequestInterceptor;
import com.netease.custom_okhttp.okhttp.chain.RequestHeaderInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealCall2 implements Call2 {

    private OkHttpClient2 okHttpClient2;
    private Request2 request2;

    public OkHttpClient2 getOkHttpClient2() {
        return okHttpClient2;
    }

    public Request2 getRequest2() {
        return request2;
    }

    public boolean isExecuted() {
        return executed;
    }

    public RealCall2(OkHttpClient2 okHttpClient2, Request2 request2) {
        this.okHttpClient2 = okHttpClient2;
        this.request2 = request2;
    }

    private boolean executed;

    @Override
    public void enqueue(Callback2 responseCallback) {
        // 不能被重复的执行 enqueue
        synchronized (this) {
            if (executed) {
                executed = true;
                throw new IllegalStateException("不能被重复的执行 enqueue Already Executed");
            }
        }

        okHttpClient2.dispatcher().enqueue(new AsyncCall2(responseCallback));
    }

    final class AsyncCall2 implements Runnable {

        public Request2 getRequest() {
            return RealCall2.this.request2;
        }

        private Callback2 callback2;

        public AsyncCall2(Callback2 callback2) {
            this.callback2 = callback2;
        }

        @Override
        public void run() {
            // 执行耗时操作
            boolean signalledCallback = false;
            try {
                Response2 response = getResponseWithInterceptorChain();
                // 如果用户取消了请求，回调给用户，说失败了
                if (okHttpClient2.getCanceled()) {
                    signalledCallback = true;
                    callback2.onFailure(RealCall2.this, new IOException("用户取消了 Canceled"));
                } else {
                    signalledCallback = true;
                    callback2.onResponse(RealCall2.this, response);
                }

            }catch (IOException e) {
                // 责任的划分
                if (signalledCallback) { // 如果等于true，回调给用户了，是用户操作的时候 报错
                    System.out.println("用户再使用过程中 出错了...");
                } else {
                    callback2.onFailure(RealCall2.this, new IOException("OKHTTP getResponseWithInterceptorChain 错误... e:" + e.toString()));
                }
            } finally {
                // 回收处理
                okHttpClient2.dispatcher().finished(this);
            }
        }

        /**
         * 责任链模式设计的拦截器
         * @return
         * @throws IOException
         */
        private Response2 getResponseWithInterceptorChain() throws IOException {
//            Response2 response2 = new Response2();
//            response2.setBody("流程走通....");
//            return response2;

            List<Interceptor2> interceptor2List = new ArrayList<>();
            interceptor2List.add(new ReRequestInterceptor()); // 重试拦截器 Response
            interceptor2List.add(new RequestHeaderInterceptor()); // 请求头拦截器 Response
            interceptor2List.add(new ConnectionServerInterceptor()); // 连接服务器的拦截器 Response

            ChainManager chainManager = new ChainManager(interceptor2List, 0, request2, RealCall2.this);
            return chainManager.getResponse(request2); // 最终返回的Response
        }
    }
}
