package com.netease.custom_okhttp.okhttp;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

public class Request2 {
    public static final String GET = "GET";
    public static final String POST = "POST";

    private String url;
    private String requestMethod = GET; // 默认请求下是GET
    private Map<String, String> mHeaderList = new HashMap<>(); // 请求头 之请求集合
    private RequestBody2 requestBody2;

    public String getUrl() {
        return url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public Map<String, String> getmHeaderList() {
        return mHeaderList;
    }

    public RequestBody2 getRequestBody2() {
        return requestBody2;
    }

    public Request2() {
        this(new Builder());
    }

    public Request2(Builder builder) {
        this.url = builder.url;
        this.requestMethod = builder.requestMethod;
        this.mHeaderList = builder.mHeaderList;
        this.requestBody2 = builder.requestBody2;
    }

    public final static class Builder {

        private String url;
        private String requestMethod = GET; // 默认请求下是GET
        private Map<String, String> mHeaderList = new HashMap<>();
        private RequestBody2 requestBody2;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder get() {
            requestMethod = GET;
            return this;
        }

        public Builder post(RequestBody2 requestBody2) {
            requestMethod = POST;
            this.requestBody2 = requestBody2;
            return this;
        }

        /**
         * Connection: keep-alive
         * Host: restapi.amap.com
         * @return
         */
        public Builder addRequestHeader(String key, String value) {
            mHeaderList.put(key, value);
            return this;
        }

        public Request2 build() {
            return new Request2(this);
        }

    }

}
