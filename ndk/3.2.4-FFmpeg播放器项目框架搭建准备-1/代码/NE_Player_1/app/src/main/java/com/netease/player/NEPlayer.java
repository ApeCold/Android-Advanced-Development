package com.netease.player;

import javax.xml.transform.ErrorListener;

public class NEPlayer {
    static {
        System.loadLibrary("native-lib");
    }


    //直播地址或媒体文件路径
    private String dataSource;

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 播放准备工作
     */
    public void prepare() {
        prepareNative(dataSource);
    }

    /**
     * 给jni回调用的
     * errorCode 从jni 反射传递过来
     * @param errorCode
     */
    public void onError(int errorCode){
        listener.onError(errorCode);
    }

    void setListener(MyErrorListener listener){
        this.listener = listener;
    }
    interface  MyErrorListener{
        void onError(int errorCode);
    }

    MyErrorListener listener;

    private native void prepareNative(String dataSource);
}
