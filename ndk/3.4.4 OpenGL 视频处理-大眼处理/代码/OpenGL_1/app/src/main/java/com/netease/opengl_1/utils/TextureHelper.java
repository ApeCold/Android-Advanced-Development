package com.netease.opengl_1.utils;

import static android.opengl.GLES20.*;

public class TextureHelper {
    /**
     * 生成并配置纹理
     * @param textures
     */
    public  static  void genTextures(int[] textures){
        glGenTextures(textures.length, textures, 0);
        for(int i = 0; i < textures.length; i++){
            //1，绑定纹理
            // 面向过程
            // 绑定后的操作就是在该纹理上进行的
            // int target, 纹理目标
            // int texture 纹理id
            glBindTexture(GL_TEXTURE_2D, textures[i]);

            //2，配置纹理
            //2.1 设置过滤参数，当纹理被使用到一个比它大或小的形状上时，opengl该如何处理
            //配合使用：min与最近点，mag 与 线性采样
            //int target,   纹理目标
            //int pname,    参数名
            //int param     参数值
            //GL_TEXTURE_MAG_FILTER 放大过滤
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            //GL_TEXTURE_MIN_FILTER 缩小过滤
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);


            //2.2 设置纹理环绕方向，纹理坐标0-1，如果超出范围的坐标，告诉opengl根据配置的参数进行处理
            //GL_TEXTURE_WRAP_S GL_TEXTURE_WRAP_T分别为纹理的x, y 方向
            //GL_REPEAT 重复拉伸（平铺）
            //GL_CLAMP_TO_EDGE 截取拉伸（边缘拉伸）
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            //3，解绑纹理（传0 表示与当前纹理解绑）
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
}
