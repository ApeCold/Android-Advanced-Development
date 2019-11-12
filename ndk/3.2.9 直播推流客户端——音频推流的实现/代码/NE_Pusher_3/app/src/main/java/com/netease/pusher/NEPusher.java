package com.netease.pusher;

import android.app.Activity;
import android.view.SurfaceHolder;

public class NEPusher {

    static {
        System.loadLibrary("native-lib");
    }

    private VideoChannel videoChannel;
    private AudioChannel audioChannel;


    public NEPusher(Activity activity, int cameraId, int width, int height, int fps, int bitrate) {
        native_init();
        videoChannel = new VideoChannel(this, activity, cameraId, width, height, fps, bitrate);
        audioChannel = new AudioChannel(this);
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        videoChannel.setPreviewDisplay(holder);
    }

    public void switchCamera() {
        videoChannel.switchCamera();
    }

    /**
     * 开始直播
     *
     * @param path rtmp地址
     */
    public void startLive(String path) {
        //native层
        native_start(path);//创建推流服务器的连接以及发送包的线程
        videoChannel.startLive();//采集相机视频数据并编码发送
        audioChannel.startLive();//采集手机麦克风的音频数据并编码发送
    }

    public void stopLive() {
        videoChannel.stopLive();
        audioChannel.stopLive();
        native_stop();
    }

    public int getInputSamples() {
        return native_getInputSamples();
    }
    public void release() {
        videoChannel.release();
        audioChannel.release();
        native_release();
    }
    /**
     * 初始化
     */
    public native void native_init();

    /**
     * 开始直播
     *
     * @param path
     */
    public native void native_start(String path);

    /**
     * 停止直播
     */
    public native void native_stop();

    public native void native_release();

    public native void native_pushVideo(byte[] data);

    public native int native_getInputSamples();

    public native void native_initVideoEncoder(int width, int height, int mFps, int bitrate);

    public native void native_initAudioEncoder(int sampleRate, int numChannels);

    public native void native_pushAudio(byte[] bytes);
}

