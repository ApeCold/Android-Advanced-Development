package com.netease.opengl_1.filters;

import android.content.Context;
import android.opengl.GLES11Ext;

import com.netease.opengl_1.R;
import com.netease.opengl_1.utils.BufferHelper;
import com.netease.opengl_1.utils.ShaderHelper;
import com.netease.opengl_1.utils.TextResourceReader;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

public abstract class BaseFilter {

    private final int mVertexSouceId;
    private final int mFragmentSouceId;
    protected final FloatBuffer vertexData;
    protected final FloatBuffer textureData;
    protected int mProgramId;
    protected int vPosition;
    protected int vCoord;
    protected int vMatrix;
    protected int vTexture;
    protected int mWidth;
    protected int mHeight;

    public BaseFilter(Context context, int vertexSouceId, int fragmentSouceId) {
        mVertexSouceId = vertexSouceId;
        mFragmentSouceId = fragmentSouceId;

        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };
        vertexData = BufferHelper.getFloatBuffer(VERTEX);

        float[] TEXTURE = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
        textureData = BufferHelper.getFloatBuffer(TEXTURE);

        init(context);
        changeTextureData();
    }

    /**
     * 修改纹理坐标 textureData（有需求可以重写该方法）
     */
    protected void changeTextureData(){

    }

    protected void init(Context context) {
        //顶点着色器代码
        String vertexSource = TextResourceReader.readTextFileFromResource(context,
                mVertexSouceId);
        //片元着色器代码
        String fragmentSource = TextResourceReader.readTextFileFromResource(context,
                mFragmentSouceId);
        //编译获得着色器id
        int vertexShaderId = ShaderHelper.compileVertexShader(vertexSource);
//        int fragmentShaderId = ShaderHelper.compileVertexShader(fragmentSource);//Bug
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentSource);
        //获取程序id
        mProgramId = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
        //通过程序id获取属性索引
        //顶点
        vPosition = glGetAttribLocation(mProgramId, "vPosition");
        vCoord = glGetAttribLocation(mProgramId, "vCoord");
        vMatrix = glGetUniformLocation(mProgramId, "vMatrix");
        //片元
        vTexture = glGetUniformLocation(mProgramId, "vTexture");
    }

    public void release(){
        glDeleteProgram(mProgramId);
    }

    public void onReady(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int onDrawFrame(int textureID) {
        //1， 设置视窗
        glViewport(0, 0, mWidth, mHeight);
        //2，使用着色器程序
        glUseProgram(mProgramId);

        //渲染？ 传值
        //1，顶点数据
        vertexData.position(0);

        //int indx,
        //        int size,
        //        int type,
        //        boolean normalized,
        //        int stride,
        //        java.nio.Buffer ptr
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, vertexData);//传值
        //传值后激活
        glEnableVertexAttribArray(vPosition);

        //2，纹理坐标
        textureData.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, textureData);
        //传值后激活
        glEnableVertexAttribArray(vCoord);



        //片元， vTexture
        //激活图层
        glActiveTexture(GL_TEXTURE0);
        //绑定
        glBindTexture(GL_TEXTURE_2D, textureID);
        //传递参数
        glUniform1i(vTexture, 0);


        //通知opengl绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        return textureID;
    }
}
