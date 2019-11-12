package com.dongnao.livedemo.core;

import android.graphics.SurfaceTexture;

import com.dongnao.livedemo.core.listener.RESScreenShotListener;
import com.dongnao.livedemo.core.listener.RESVideoChangeListener;
import com.dongnao.livedemo.encoder.MediaVideoEncoder;
import com.dongnao.livedemo.model.RTMPConfig;
import com.dongnao.livedemo.model.RTMPCoreParameters;
import com.dongnao.livedemo.rtmp.RESFlvDataCollecter;


public interface RESVideoCore {
    int OVERWATCH_TEXTURE_ID = 10;
    boolean prepare(RTMPConfig resConfig);

    void updateCamTexture(SurfaceTexture camTex);

    void startPreview(SurfaceTexture surfaceTexture, int visualWidth, int visualHeight);

    void updatePreview(int visualWidth, int visualHeight);

    void stopPreview(boolean releaseTexture);

    boolean startStreaming(RESFlvDataCollecter flvDataCollecter);

    boolean stopStreaming();

    boolean destroy();

    void reSetVideoBitrate(int bitrate);

    int getVideoBitrate();

    void reSetVideoFPS(int fps);

    void reSetVideoSize(RTMPCoreParameters newParameters);

    void setCurrentCamera(int cameraIndex);

    void takeScreenShot(RESScreenShotListener listener);

    void setVideoChangeListener(RESVideoChangeListener listener);

    float getDrawFrameRate();

    void setVideoEncoder(final MediaVideoEncoder encoder);

    void setMirror(boolean isEnableMirror, boolean isEnablePreviewMirror, boolean isEnableStreamMirror);
    void setNeedResetEglContext(boolean bol);


}
