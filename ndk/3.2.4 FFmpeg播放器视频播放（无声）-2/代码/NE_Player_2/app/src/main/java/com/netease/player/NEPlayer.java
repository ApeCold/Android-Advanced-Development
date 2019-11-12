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
     * 开始播放
     */
    public void start() {
        startNative();
    }

    /**
     * 供native反射调用
     * 表示播放器准备好了可以开始播放了
     */
    public void onPrepared() {
        if (onpreparedListener != null) {
            onpreparedListener.onPrepared();
        }
    }

    void setOnpreparedListener(OnpreparedListener onpreparedListener) {
        this.onpreparedListener = onpreparedListener;
    }


    interface OnpreparedListener {
        void onPrepared();
    }

    private OnpreparedListener onpreparedListener;

    private native void prepareNative(String dataSource);
    private native void startNative();
    public  static native void staticTest();
}
