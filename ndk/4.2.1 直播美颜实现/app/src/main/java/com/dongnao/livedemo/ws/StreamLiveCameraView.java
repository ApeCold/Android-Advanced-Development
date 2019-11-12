package com.dongnao.livedemo.ws;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.dongnao.livedemo.client.RTMPClient;
import com.dongnao.livedemo.core.listener.RESConnectionListener;
import com.dongnao.livedemo.core.listener.RESScreenShotListener;
import com.dongnao.livedemo.core.listener.RESVideoChangeListener;
import com.dongnao.livedemo.encoder.MediaAudioEncoder;
import com.dongnao.livedemo.encoder.MediaEncoder;
import com.dongnao.livedemo.encoder.MediaMuxerWrapper;
import com.dongnao.livedemo.encoder.MediaVideoEncoder;
import com.dongnao.livedemo.filter.hardvideofilter.BaseHardVideoFilter;
import com.dongnao.livedemo.model.RTMPConfig;
import com.dongnao.livedemo.model.Size;
import com.dongnao.livedemo.tools.CameraUtil;
import com.dongnao.livedemo.ws.filter.audiofilter.SetVolumeAudioFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by WangShuo on 2017/6/11.
 */

public class StreamLiveCameraView extends FrameLayout {

    private static final String TAG = "StreamLiveCameraView";

    private Context mContext;
    private AspectTextureView textureView;
    private final List<RESConnectionListener> outerStreamStateListeners = new ArrayList<>();

    private static RTMPClient rtmpClient;
    private static RTMPConfig resConfig;
    private static int quality_value_min = 400 * 1024;
    private static int quality_value_max = 700 * 1024;

    public StreamLiveCameraView(Context context) {
        super(context);
        this.mContext=context;
    }

    public StreamLiveCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
    }

    public static synchronized RTMPClient getRESClient() {
        if (rtmpClient == null) {
            rtmpClient = new RTMPClient();
        }
        return rtmpClient;
    }

    /**
     * 根据AVOption初始化&打开预览
     * @param avOption
     */
    public void init(Context context , StreamAVOption avOption) {
        if (avOption == null) {
            throw new IllegalArgumentException("AVOption is null.");
        }
        compatibleSize(avOption);
        rtmpClient = getRESClient();
        setContext(mContext);
        resConfig = StreamConfig.build(context,avOption);
        boolean isSucceed = rtmpClient.prepare(resConfig);
        if (!isSucceed) {
            Log.w(TAG, "推流prepare方法返回false, 状态异常.");
            return;
        }
        initPreviewTextureView();
        addListenerAndFilter();
    }

    private void compatibleSize(StreamAVOption avOptions) {
        Camera.Size cameraSize = CameraUtil.getInstance().getBestSize(CameraUtil.getFrontCameraSize(), Integer.parseInt("800"));
        if(!CameraUtil.hasSupportedFrontVideoSizes){
            if(null == cameraSize || cameraSize.width <= 0){
                avOptions.videoWidth = 720;
                avOptions.videoHeight = 480;
            }else{
                avOptions.videoWidth = cameraSize.width;
                avOptions.videoHeight = cameraSize.height;
            }
        }
    }

    private void initPreviewTextureView() {
        if (textureView == null && rtmpClient != null) {
            textureView = new AspectTextureView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            this.removeAllViews();
            this.addView(textureView);
            textureView.setKeepScreenOn(true);
            textureView.setSurfaceTextureListener(surfaceTextureListenerImpl);
            Size s = rtmpClient.getVideoSize();
            textureView.setAspectRatio(AspectTextureView.MODE_OUTSIDE, ((double) s.getWidth() / s.getHeight()));
        }
    }

    private void addListenerAndFilter() {
        if (rtmpClient != null) {
            rtmpClient.setConnectionListener(ConnectionListener);
            rtmpClient.setVideoChangeListener(VideoChangeListener);
            rtmpClient.setSoftAudioFilter(new SetVolumeAudioFilter());
        }
    }

    /**
     * 是否推流
     */
    public boolean isStreaming(){
        if(rtmpClient != null){
           return rtmpClient.isStreaming;
        }
        return false;
    }

    /**
     * 开始推流
     */
    public void startStreaming(String rtmpUrl){
        if(rtmpClient != null){
            rtmpClient.startStreaming(rtmpUrl);
        }
    }

    /**
     * 停止推流
     */
    public void stopStreaming(){
        if(rtmpClient != null){
            rtmpClient.stopStreaming();
        }
    }

    /**
     * 开始录制
     */
    private MediaMuxerWrapper mMuxer;
    private boolean isRecord = false;
    public void startRecord(){
        if(rtmpClient != null){
            rtmpClient.setNeedResetEglContext(true);
            try {
                mMuxer = new MediaMuxerWrapper(".mp4");    // if you record audio only, ".m4a" is also OK.
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, StreamAVOption.recordVideoWidth, StreamAVOption.recordVideoHeight);
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

                mMuxer.prepare();
                mMuxer.startRecording();
                isRecord = true;
            } catch (IOException e) {
                isRecord = false;
                e.printStackTrace();
            }
        }
    }
    /**
     * 停止录制
     */
    public String stopRecord() {
        isRecord = false;
        if (mMuxer != null) {
            String path = mMuxer.getFilePath();
            mMuxer.stopRecording();
            mMuxer = null;
            System.gc();
            return path;
        }
        System.gc();
        return null;
    }

    /**
     * 是否在录制
     */
    public boolean isRecord() {
        return isRecord;
    }

    /**
     * 切换摄像头
     */
    public void swapCamera(){
        if(rtmpClient != null){
            rtmpClient.swapCamera();
        }
    }

    /**
     * 摄像头焦距 [0.0f,1.0f]
     */
    public void setZoomByPercent(float targetPercent){
        if(rtmpClient != null){
            rtmpClient.setZoomByPercent(targetPercent);
        }
    }

    /**
     *摄像头开关闪光灯
     */
    public void toggleFlashLight(){
        if(rtmpClient != null){
            rtmpClient.toggleFlashLight();
        }
    }

    /**
     * 推流过程中，重新设置帧率
     */
    public void reSetVideoFPS(int fps){
        if(rtmpClient != null){
            rtmpClient.reSetVideoFPS(fps);
        }
    }

    /**
     * 推流过程中，重新设置码率
     */
    public void reSetVideoBitrate(int bitrate){
        if(rtmpClient != null){
            rtmpClient.reSetVideoBitrate(bitrate);
        }
    }

    /**
     * 截图
     */
    public void takeScreenShot(RESScreenShotListener listener){
        if(rtmpClient != null){
            rtmpClient.takeScreenShot(listener);
        }
    }

    /**
     * 镜像
     * @param isEnableMirror   是否启用镜像功能 总开关
     * @param isEnablePreviewMirror  是否开启预览镜像
     * @param isEnableStreamMirror   是否开启推流镜像
     */
    public void setMirror(boolean isEnableMirror,boolean isEnablePreviewMirror,boolean isEnableStreamMirror) {
        if(rtmpClient != null) {
            rtmpClient.setMirror(isEnableMirror, isEnablePreviewMirror, isEnableStreamMirror);
        }
    }


    /**
     * 设置滤镜
     */
    public void setHardVideoFilter(BaseHardVideoFilter baseHardVideoFilter){
        if(rtmpClient != null){
            rtmpClient.setHardVideoFilter(baseHardVideoFilter);
        }
    }

    /**
     * 获取BufferFreePercent
     */
    public float getSendBufferFreePercent() {
        return rtmpClient.getSendBufferFreePercent();
    }

    /**
     * AVSpeed 推流速度 和网络相关
     */
    public int getAVSpeed() {
        return rtmpClient.getAVSpeed();
    }

    /**
     * 设置上下文
     */
    public void setContext(Context context){
        if(rtmpClient != null){
            rtmpClient.setContext(context);
        }
    }

    /**
     * destroy
     */
    public void destroy(){
        if (rtmpClient != null) {
            rtmpClient.setConnectionListener(null);
            rtmpClient.setVideoChangeListener(null);
            if(rtmpClient.isStreaming){
                rtmpClient.stopStreaming();
            }
            if(isRecord()){
                stopRecord();
            }
            rtmpClient.destroy();
        }
    }

    /**
     * 添加推流状态监听
     * @param listener
     */
    public void addStreamStateListener(RESConnectionListener listener) {
        if (listener != null && !outerStreamStateListeners.contains(listener)) {
            outerStreamStateListeners.add(listener);
        }
    }

    RESConnectionListener ConnectionListener =new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            if(result == 1){
               rtmpClient.stopStreaming();
            }

            for (RESConnectionListener listener: outerStreamStateListeners) {
                listener.onOpenConnectionResult(result);
            }
        }

        @Override
        public void onWriteError(int errno) {

            for (RESConnectionListener listener: outerStreamStateListeners) {
                listener.onWriteError(errno);
            }
        }

        @Override
        public void onCloseConnectionResult(int result) {

            for (RESConnectionListener listener: outerStreamStateListeners) {
                listener.onCloseConnectionResult(result);
            }
        }
    };

    RESVideoChangeListener VideoChangeListener = new RESVideoChangeListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            if(textureView != null) {
                textureView.setAspectRatio(AspectTextureView.MODE_INSIDE, ((double) width) / height);
            }
        }
    };

    TextureView.SurfaceTextureListener surfaceTextureListenerImpl  = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (rtmpClient != null) {
                rtmpClient.startPreview(surface, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (rtmpClient != null) {
                rtmpClient.updatePreview(width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (rtmpClient != null) {
                rtmpClient.stopPreview(true);
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    /**
     * callback methods from encoder
     */
    MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder && rtmpClient != null)
                rtmpClient.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder && rtmpClient != null)
                rtmpClient.setVideoEncoder(null);
        }
    };
}
