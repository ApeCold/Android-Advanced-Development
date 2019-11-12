package com.wangyi.webrtc.ws;

/**
 * Created by dds on 2019/4/5.
 * android_shuai@163.com
 */
public interface IConnectEvent {

    void onSuccess();

    void onFailed(String msg);
}
