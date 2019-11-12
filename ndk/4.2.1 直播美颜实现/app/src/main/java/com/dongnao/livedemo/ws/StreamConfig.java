package com.dongnao.livedemo.ws;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;

import com.dongnao.livedemo.model.RTMPConfig;
import com.dongnao.livedemo.model.Size;

/**
 * Created by WangShuo on 2017/6/11.
 */

public class StreamConfig {

    public static class AVOptionsHolder {

        public static final int DEFAULT_CAMERA_INDEX = Camera.CameraInfo.CAMERA_FACING_FRONT;

        public static final int DEFAULT_FILTER_MODE = RTMPConfig.FilterMode.HARD;

        public static final int DEFAULT_RENDER_MODE = RTMPConfig.RenderingMode.OpenGLES;

        public static final int DEFAULT_PREVIEW_WIDTH = 1280;

        public static final int DEFAULT_PREVIEW_HEIGHT = 720;

        public static final int DEFAULT_VIDEO_WIDTH = 640;

        public static final int DEFAULT_VIDEO_HEIGHT = 360;

        public static final int DEFAULT_VIDEO_BITRATE =600 * 1024;

        public static final int DEFAULT_VIDEO_FPS = 20;

        public static final int DEFAULT_VIDEO_GOP = 2;

    }

    public static RTMPConfig build(Context context, StreamAVOption option) {
        RTMPConfig res = RTMPConfig.obtain();
        res.setFilterMode(AVOptionsHolder.DEFAULT_FILTER_MODE);
        res.setRenderingMode(AVOptionsHolder.DEFAULT_RENDER_MODE);
        res.setTargetPreviewSize(new Size(option.previewWidth,option.previewHeight));
        res.setTargetPreviewSize(new Size(option.previewWidth,option.previewHeight));
        res.setTargetVideoSize(new Size(option.videoWidth, option.videoHeight));
        res.setBitRate(option.videoBitrate);
        res.setVideoFPS(option.videoFramerate);
        res.setVideoGOP(option.videoGOP);
        res.setDefaultCamera(option.cameraIndex);
        res.setRtmpAddr(option.streamUrl);

        int frontDirection, backDirection;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
        frontDirection = cameraInfo.orientation;
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
        backDirection = cameraInfo.orientation;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            res.setFrontCameraDirectionMode((frontDirection == 90 ? RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270 : RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90) | RTMPConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL);
            res.setBackCameraDirectionMode((backDirection == 90 ? RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90 : RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270));
        } else {
            res.setBackCameraDirectionMode((backDirection == 90 ? RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0 : RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180));
            res.setFrontCameraDirectionMode((frontDirection == 90 ? RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RTMPConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0) | RTMPConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL);
        }
        return res;
    }


}
