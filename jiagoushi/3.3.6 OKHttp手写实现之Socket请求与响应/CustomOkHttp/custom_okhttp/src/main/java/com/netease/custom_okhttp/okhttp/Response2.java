package com.netease.custom_okhttp.okhttp;

// 响应的result信息
public class Response2 {

    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String string() {
        return body;
    }


}
