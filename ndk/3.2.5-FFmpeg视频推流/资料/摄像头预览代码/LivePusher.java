package com.wangyi.wangyipushdemo2;

import android.app.Activity;
import android.view.SurfaceHolder;

import com.wangyi.wangyipushdemo2.meida.AudioChannel;
import com.wangyi.wangyipushdemo2.meida.VideoChannel;

public class LivePusher {
    private AudioChannel audioChannel;
    private VideoChannel videoChannel;
    static {
        System.loadLibrary("native-lib");
    }

    public LivePusher(Activity activity, int width, int height, int bitrate,
                      int fps, int cameraId) {
        videoChannel = new VideoChannel(this, activity, width, height, bitrate, fps, cameraId);
        audioChannel = new AudioChannel(this);


    }
    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        videoChannel.setPreviewDisplay(surfaceHolder);
    }
    public void switchCamera() {
        videoChannel.switchCamera();
    }
}
