package com.dongnao.livedemo.filter.hardvideofilter;

import com.dongnao.livedemo.core.GLHelper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;



public class BaseHardVideoFilter {
    protected int SIZE_WIDTH;
    protected int SIZE_HEIGHT;
    protected int directionFlag=-1;
    protected ShortBuffer drawIndecesBuffer;

    public void onInit(int VWidth, int VHeight) {
        SIZE_WIDTH = VWidth;
        SIZE_HEIGHT = VHeight;
        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
    }

    public void onDraw(final int cameraTexture, final int targetFrameBuffer, final FloatBuffer shapeBuffer, final FloatBuffer textrueBuffer) {
    }

    public void onDestroy() {

    }

    public void onDirectionUpdate(int _directionFlag) {
        this.directionFlag = _directionFlag;
    }
}
