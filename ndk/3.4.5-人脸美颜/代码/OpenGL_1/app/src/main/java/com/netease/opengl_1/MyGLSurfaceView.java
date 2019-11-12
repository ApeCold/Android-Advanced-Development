package com.netease.opengl_1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;
    private Speed mSpeed = Speed.MODE_NORMAL;



    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }

    public MyGLSurfaceView(Context context) {
        super(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化GLSurfaceView
     */
    private void init() {
        //1, 设置EGL版本
        setEGLContextClientVersion(2);
        //2, 设置渲染器
        mRenderer = new MyGLRenderer(this);
        setRenderer(mRenderer);
        //3, 设置渲染模式：按需渲染
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mRenderer.onSurfaceDestroyed();
    }

    /**
     * 开始录制
     */
    public void startRecording() {
        float speed = 1.0f;
        switch (mSpeed){
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.0f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 3.0f;
                break;

        }
        mRenderer.startRecording(speed);
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        mRenderer.stopRecording();
    }

    public void setSpeed(Speed speed) {
        mSpeed = speed;
    }

    public void enableBigEye(boolean isChecked) {
        mRenderer.enableBigEye(isChecked);
    }
    public void enableStick(boolean isChecked) {
        mRenderer.enableStick(isChecked);
    }
    public void enableBeauty(boolean isChecked) {
        mRenderer.enableBeauty(isChecked);
    }
}
