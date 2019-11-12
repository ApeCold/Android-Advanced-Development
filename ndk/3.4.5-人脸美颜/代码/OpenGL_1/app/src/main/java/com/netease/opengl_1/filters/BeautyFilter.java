package com.netease.opengl_1.filters;

import android.content.Context;

import com.netease.opengl_1.R;

import static android.opengl.GLES20.*;

public class BeautyFilter extends BaseFrameFilter {

    private final int width;
    private final int height;

    public BeautyFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.beauty_fragment);

        width = glGetUniformLocation(mProgramId, "width");
        height = glGetUniformLocation(mProgramId, "height");

    }

    @Override
    public int onDrawFrame(int textureID) {

        glViewport(0, 0, mWidth, mHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0]);

        glUseProgram(mProgramId);

        vertexData.position(0);
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, vertexData);//传值
        glEnableVertexAttribArray(vPosition);

        textureData.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, textureData);
        glEnableVertexAttribArray(vCoord);

        glUniform1i(width, mWidth);
        glUniform1i(height, mHeight);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glUniform1i(vTexture, 0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return mFrameBufferTextures[0];//返回fbo的纹理id

    }
}
