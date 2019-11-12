package com.dongnao.wangyipalyer;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WangyiPlayer implements SurfaceHolder.Callback{
    static {
        System.loadLibrary("wangyiplayer");
    }
    private SurfaceHolder surfaceHolder;
    public void setSurfaceView(SurfaceView surfaceView) {
        if (null != this.surfaceHolder) {
            this.surfaceHolder.removeCallback(this);
        }
        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);

    }
    public void start(String path) {
        native_start(path,surfaceHolder.getSurface());
    }
    public  native void native_start(String path,Surface surface);
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
