package com.netease.opengl_1.filters;

import android.content.Context;
import android.opengl.GLES11Ext;

import com.netease.opengl_1.R;
import com.netease.opengl_1.utils.TextureHelper;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glBindTexture;

public class CameraFilter extends BaseFrameFilter {

    private float[] matrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_fragment);
    }

    @Override
    public int onDrawFrame(int textureID) {
        //1， 设置视窗
        glViewport(0, 0, mWidth, mHeight);
        //这里是因为要渲染到FBO缓存中，而不是直接显示到屏幕上
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0]);

        //2，使用着色器程序
        glUseProgram(mProgramId);

        //渲染？ 传值
        //1，顶点数据
        vertexData.position(0);

        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, vertexData);//传值
        //传值后激活
        glEnableVertexAttribArray(vPosition);

        //2，纹理坐标
        textureData.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, textureData);
        //传值后激活
        glEnableVertexAttribArray(vCoord);

        //3, 变换矩阵
        glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);

        //片元， vTexture
        //激活图层
        glActiveTexture(GL_TEXTURE0);
        //绑定
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        //传递参数
        glUniform1i(vTexture, 0);

        //通知opengl绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        //解绑fbo
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        return textureID;//Bug
        return mFrameBufferTextures[0];//返回fbo的纹理id
    }

    public void setMatrix(float[] mtx) {
        matrix = mtx;
    }

    @Override
    protected void changeTextureData() {

    }
}
