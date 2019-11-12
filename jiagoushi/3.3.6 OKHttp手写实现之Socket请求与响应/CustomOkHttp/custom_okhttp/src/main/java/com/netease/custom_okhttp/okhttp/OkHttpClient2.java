package com.netease.custom_okhttp.okhttp;

import okhttp3.Dispatcher;

public class OkHttpClient2 {

    Dispatcher2 dispatcher;

    boolean isCanceled;

    int recount;

    public boolean getCanceled() {
        return isCanceled;
    }

    public int getRecount() {
        return recount;
    }

    public OkHttpClient2() {
        this(new Builder());
    }

    public OkHttpClient2(Builder builder) {
        dispatcher = builder.dispatcher;
        isCanceled = builder.isCanceled;
        recount = builder.recount;
    }

    public final static class Builder {

        Dispatcher2 dispatcher;

        boolean isCanceled;

        int recount = 3; // 重试次数

        public Builder() {
            dispatcher = new Dispatcher2();
        }

        public Builder dispatcher(Dispatcher2 dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        // 用户取消请求
        public Builder canceled() {
            isCanceled = true;
            return this;
        }

        public Builder setReCount(int recount) {
            this.recount = recount;
            return this;
        }

        public OkHttpClient2 build() {
            return new OkHttpClient2(this);
        }

    }

    public Call2 newCall(Request2 request2) {
        // RealCall
        return new RealCall2(this, request2);
    }

    public Dispatcher2 dispatcher() {
        return dispatcher;
    }

}
