package com.netease.pusher;

import android.app.Activity;
import android.view.SurfaceHolder;

public class NEPusher {

    static {
        System.loadLibrary("native-lib");
    }

    private VideoChannel videoChannel;
//    private AudioChannel audioChannel;


    public NEPusher(Activity activity, int cameraId, int width, int height,int fps, int bitrate) {
        native_init();
        videoChannel  = new VideoChannel(this, activity, cameraId, width, height, fps, bitrate);
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        videoChannel.setPreviewDisplay(holder);
    }

    public void switchCamera() {
        videoChannel.switchCamera();
    }

    /**
     * 开始直播
     * @param path rtmp地址
     */
    public void startLive(String path) {
        //native层
        native_start(path);
        videoChannel.startLive();
//        audioChannel.startLive();
    }

    public void stopLive() {
        videoChannel.stopLive();
//        audioChannel.stopLive();
        native_stop();
    }

    /**
     * 初始化
     */
    public native void native_init();

    /**
     * 开始直播
     * @param path
     */
    public native void native_start(String path);

    /**
     * 停止直播
     */
    public native void native_stop();

    public native void native_pushVideo(byte[] data);

    public native void native_initVideoEncoder(int width, int height, int mFps, int bitrate);
}

