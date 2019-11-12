//预览相机的着色器，顶点着色器不变，需要修改片元着色器，
//不再用sampler2D采样，需要使用samplerExternalOES 纹理采样器，
//并且要在头部增加使用扩展纹理的声明#extension GL_OES_EGL_image_external : require。
#extension GL_OES_EGL_image_external : require

//float数据是什么精度的
precision mediump float;

//采样点的坐标
varying vec2 aCoord;

//采样器
uniform samplerExternalOES vTexture;

void main(){
    //变量 接收像素值
    // texture2D：采样器 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
    gl_FragColor = texture2D(vTexture, aCoord);

    //305911公式
//    vec4 rgba =texture2D(vTexture, aCoord);
//    float gray = (0.30 * rgba.r   + 0.59 * rgba.g + 0.11* rgba.b);
//    gl_FragColor = vec4(gray, gray, gray, 1.0);
}