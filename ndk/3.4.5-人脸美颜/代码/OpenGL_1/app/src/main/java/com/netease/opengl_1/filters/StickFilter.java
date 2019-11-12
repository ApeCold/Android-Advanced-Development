package com.netease.opengl_1.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.netease.opengl_1.R;
import com.netease.opengl_1.face.Face;
import com.netease.opengl_1.utils.TextureHelper;

import static android.opengl.GLES20.*;

public class StickFilter extends BaseFrameFilter {

    private final Bitmap mBitmap;
    private int[] mTextureId;
    private Face mFace;

    public void setFace(Face mFace) {
        this.mFace = mFace;
    }

    public StickFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_fragment);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.erduo_000);
    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
        //贴图的纹理id
        mTextureId = new int[1];
        TextureHelper.genTextures(mTextureId);
        glBindTexture(GL_TEXTURE_2D, mTextureId[0]);
        //绑定bitmap到纹理
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, mBitmap, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public int onDrawFrame(int textureID) {
        if (null == mFace) {
            return textureID;
        }
        //1， 设置视窗
        glViewport(0, 0, mWidth, mHeight);
        //这里是因为要渲染到FBO缓存中，而不是直接显示到屏幕上
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0]);

        glUseProgram(mProgramId);

        vertexData.position(0);
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, vertexData);//传值
        glEnableVertexAttribArray(vPosition);

        textureData.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, textureData);
        glEnableVertexAttribArray(vCoord);


        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glUniform1i(vTexture, 0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        drawStick();
        //        return textureID;//Bug
        return mFrameBufferTextures[0];//返回fbo的纹理id

    }

    /**
     * 画贴纸
     */
    private void drawStick() {
        //开启混合模式
        glEnable(GL_BLEND);
        //设置贴图的模式
        //int srcfactor, 源图因子
        //int destfactor，目标图因子
        //GL_ONE 全部绘制
        //GL_ONE_MINUS_SRC_ALPHA 1.0 - 源图颜色的alpha作为因子
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //画耳朵贴纸：不是全屏来画
        //获取人脸框的起始坐标
        float x = mFace.landmarks[0];//相对送去人脸检测的图像宽
        float y = mFace.landmarks[1];//相对送去人脸检测的图像高

        x = x / mFace.imgWidth * mWidth;
        y = y / mFace.imgHeight * mHeight;


        //1， 设置画贴纸的视窗
        int viewWidth = (int)((float)mFace.width / mFace.imgWidth * mWidth);
        int viewHeight = mBitmap.getHeight();

        glViewport((int) x, (int) y - mBitmap.getHeight() / 2, viewWidth, viewHeight);

        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0]);
        glUseProgram(mProgramId);

        vertexData.position(0);
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, vertexData);//传值
        glEnableVertexAttribArray(vPosition);

        textureData.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, textureData);
        glEnableVertexAttribArray(vCoord);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureId[0]);
        glUniform1i(vTexture, 0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        //关闭混合模式
        glDisable(GL_BLEND);
    }

    @Override
    public void release() {
        super.release();
        mBitmap.recycle();
    }
}
