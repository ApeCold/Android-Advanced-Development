package com.netease.opengl_1;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.netease.opengl_1.face.FaceTrack;
import com.netease.opengl_1.filters.BeautyFilter;
import com.netease.opengl_1.filters.BigEyeFilter;
import com.netease.opengl_1.filters.CameraFilter;
import com.netease.opengl_1.filters.ScreenFilter;
import com.netease.opengl_1.filters.StickFilter;
import com.netease.opengl_1.record.MyMediaRecorder;
import com.netease.opengl_1.utils.CameraHelper;
import com.netease.opengl_1.utils.FileUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * 核心类
 */
class MyGLRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener,
        Camera.PreviewCallback {

    private final MyGLSurfaceView mGLSurfaceView;
    private final int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;
    private ScreenFilter mScreenFilter;
    private int[] mTextureID;
    private CameraFilter mCameraFilter;
    private MyMediaRecorder mMediaRecorder;
    private FaceTrack mFaceTrack;
    private BigEyeFilter mBigEyeFilter;
    private int mWidth;
    private int mHeight;
    private StickFilter mStickFilter;
    private BeautyFilter mBeautyFilter;

    public MyGLRenderer(MyGLSurfaceView myGLSurfaceView) {
        mGLSurfaceView = myGLSurfaceView;
        FileUtil.copyAssets2SDCard(mGLSurfaceView.getContext(), "lbpcascade_frontalface.xml",
                "/sdcard/lbpcascade_frontalface.xml");
        FileUtil.copyAssets2SDCard(mGLSurfaceView.getContext(), "seeta_fa_v1.1.bin",
                "/sdcard/seeta_fa_v1.1.bin");
    }

    /**
     * 当Surface创建时回调
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraHelper = new CameraHelper((Activity) mGLSurfaceView.getContext(), mCameraID, 800,
                480);
        mCameraHelper.setPreviewCallback(this);

        //准备画布
        mTextureID = new int[1];
        //通过opengl创建一个纹理的id
        glGenTextures(mTextureID.length, mTextureID, 0);
        mSurfaceTexture = new SurfaceTexture(mTextureID[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        mScreenFilter = new ScreenFilter(mGLSurfaceView.getContext());
        mCameraFilter = new CameraFilter(mGLSurfaceView.getContext());

//        mBigEyeFilter = new BigEyeFilter(mGLSurfaceView.getContext());

        EGLContext eglContext = EGL14.eglGetCurrentContext();   //渲染线程的 EGLContext
        mMediaRecorder = new MyMediaRecorder(800, 480, "/sdcard/test.mp4", eglContext,
                mGLSurfaceView.getContext());
    }

    /**
     * Surface发生改变时回调
     *
     * @param gl
     * //这里的宽高跟相机的分辨率不是一样的！！！
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        // 创建跟踪器
        mFaceTrack = new FaceTrack("/sdcard/lbpcascade_frontalface.xml",
                "/sdcard/seeta_fa_v1.1.bin", mCameraHelper);
        //启动跟踪器
        mFaceTrack.startTrack();

        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width, height);
//        mBigEyeFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
        //      glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //设置清理屏幕的颜色
        glClearColor(255, 0, 0, 0);
        //GL_COLOR_BUFFER_BIT 颜色缓冲区
        //GL_DEPTH_WRITEMASK    深度缓冲区
        //GL_STENCIL_BUFFER_BIT 模型缓冲区
        glClear(GL_STENCIL_BUFFER_BIT);

        //输出摄像头的数据
        //更新纹理
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
        //mTextureID[0]: 摄像头的, 先渲染到FBO
        int textureId = mCameraFilter.onDrawFrame(mTextureID[0]);
        //滤镜特效
        //textureId = xxxFilter.onDrawFrame(textureId);
        //textureId = xxxFilter.onDrawFrame(textureId);
        //......

//        mBigEyeFilter.setFace(mFaceTrack.getFace());
//        textureId = mBigEyeFilter.onDrawFrame(textureId);

        if (null != mBigEyeFilter){
            mBigEyeFilter.setFace(mFaceTrack.getFace());
            textureId = mBigEyeFilter.onDrawFrame(textureId);
        }
        if (null != mStickFilter){
            mStickFilter.setFace(mFaceTrack.getFace());
            textureId = mStickFilter.onDrawFrame(textureId);
        }
        if (null != mBeautyFilter){
            textureId = mBeautyFilter.onDrawFrame(textureId);
        }
        mScreenFilter.onDrawFrame(textureId);
        //textureId 是要渲染的纹理id 也是要编码的纹理id

        //录制视频（将图像进行编码）
        mMediaRecorder.encodeFrame(textureId, mSurfaceTexture.getTimestamp());
    }

    /**
     * 有可用数据时回调
     *
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLSurfaceView.requestRender();
    }

    public void onSurfaceDestroyed() {
        mCameraHelper.stopPreview();
        mFaceTrack.stopTrack();
    }

    /**
     * 开始录制
     *
     * @param speed
     */
    public void startRecording(float speed) {
        Log.e("MyGLRender", "startRecording");
        try {
            mMediaRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        Log.e("MyGLRender", "stopRecording");
        mMediaRecorder.stop();
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mFaceTrack.detector(data);
    }

    public void enableBigEye(final boolean isChecked) {
//        mBigEyeFilter = new BigEyeFilter(mGLSurfaceView.getContext());
//        mBigEyeFilter.onReady(mWidth, mHeight);

        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Opengl线程
                if (isChecked) {
                    mBigEyeFilter = new BigEyeFilter(mGLSurfaceView.getContext());
                    mBigEyeFilter.onReady(mWidth, mHeight);
                } else {
                    mBigEyeFilter.release();
                    mBigEyeFilter = null;
                }
            }
        });
    }

    public void enableStick(final boolean isChecked) {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Opengl线程
                if (isChecked) {
                    mStickFilter = new StickFilter(mGLSurfaceView.getContext());
                    mStickFilter.onReady(mWidth, mHeight);
                } else {
                    mStickFilter.release();
                    mStickFilter = null;
                }
            }
        });
    }

    public void enableBeauty(final boolean isChecked) {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Opengl线程
                if (isChecked) {
                    mBeautyFilter = new BeautyFilter(mGLSurfaceView.getContext());
                    mBeautyFilter.onReady(mWidth, mHeight);
                } else {
                    mBeautyFilter.release();
                    mBeautyFilter = null;
                }
            }
        });
    }
}
