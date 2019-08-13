package com.wangyi.wangyipushdemo2.meida;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.wangyi.wangyipushdemo2.LivePusher;

public class VideoChannel implements Camera.PreviewCallback, CameraHelper.OnChangedSizeListener {
    private static final String TAG = "tuch";
    private CameraHelper cameraHelper;
    private int mBitrate;
    private int mFps;
    private boolean isLiving;
    public VideoChannel(LivePusher livePusher, Activity activity, int width, int height, int bitrate, int fps, int cameraId) {
        mBitrate = bitrate;
        mFps = fps;
        cameraHelper = new CameraHelper(activity, cameraId, width, height);
        cameraHelper.setPreviewCallback(this);
        cameraHelper.setOnChangedSizeListener(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i(TAG, "获取到一帧   onPreviewFrame: ");
    }

    @Override
    public void onChanged(int w, int h) {

    }
    public void switchCamera() {
        cameraHelper.switchCamera();
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        cameraHelper.setPreviewDisplay(surfaceHolder);
    }
}
