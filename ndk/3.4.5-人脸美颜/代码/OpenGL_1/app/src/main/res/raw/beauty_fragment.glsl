precision mediump float;

varying vec2 aCoord;

uniform sampler2D vTexture;

//纹理宽、高
uniform int width;
uniform int height;

vec2 blurCoordinates[20];

void main(){
    vec2 singleStepOffset = vec2(1.0/float(width), 1.0/float(height));

    // 1, 高斯模糊
    //采集20个点
    blurCoordinates[0] = aCoord.xy + singleStepOffset * vec2(0.0, -10.0);
    blurCoordinates[1] = aCoord.xy + singleStepOffset * vec2(0.0, 10.0);
    blurCoordinates[2] = aCoord.xy + singleStepOffset * vec2(-10.0, 0.0);
    blurCoordinates[3] = aCoord.xy + singleStepOffset * vec2(10.0, 0.0);
    blurCoordinates[4] = aCoord.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = aCoord.xy + singleStepOffset * vec2(5.00, 8.0);
    blurCoordinates[7] = aCoord.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[6] = aCoord.xy + singleStepOffset * vec2(-5., -8.0);
    blurCoordinates[8] = aCoord.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = aCoord.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = aCoord.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = aCoord.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = aCoord.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = aCoord.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = aCoord.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = aCoord.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = aCoord.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = aCoord.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = aCoord.xy + singleStepOffset * vec2(4.0, -4.0);
    blurCoordinates[19] = aCoord.xy + singleStepOffset * vec2(4.0, 4.0);

    //当前采样点的像素值
    vec4 currentColor = texture2D(vTexture, aCoord);
    vec3 rgb = currentColor.rgb;

    //  计算坐标颜色值的总和
    for (int i = 0; i < 20; i++){
        //采集20个点的像素
        rgb += texture2D(vTexture, blurCoordinates[i].xy).rgb;
    }
    //rgb： 21个点的总和
    vec4 blur = vec4(rgb *1.0/21.0, currentColor.a);
    //    gl_FragColor = blur;

    //2，高反差公式： 高反差保留 = 原图 - 高斯模糊图
    vec4 highPassColor = currentColor - blur;
    //强度系数
    highPassColor.r = clamp(2.0*highPassColor.r*highPassColor.r * 24.0, 0.0, 1.0);
    highPassColor.g = clamp(2.0*highPassColor.g*highPassColor.g * 24.0, 0.0, 1.0);
    highPassColor.b = clamp(2.0*highPassColor.b*highPassColor.b * 24.0, 0.0, 1.0);

    vec4 highPassBlur = vec4(highPassColor.rgb, 1.0);
//    vec4 test ;
//    test.r = 1.0;
//    test.x = 1.0;
//    test.s = 1.0;
    //去痘印，疤痕等
    //    gl_FragColor = highPassBlur;

    //3, 磨皮

    //蓝色分量
    float blue = min(currentColor.b, blur.b);
    //max(min(color.r, 1.0), 0.0);
    float value = clamp((blue - 0.2) * 5.0, 0.0, 1.0);

    //取r, g, b 三个分量中最大的值
    float maxChannelColor = max(max(highPassColor.r, highPassColor.g), highPassColor.b);

    float intensity = 1.0;//磨皮强度
    float currentIntensity = (1.0 - maxChannelColor / (maxChannelColor + 0.2)) * value * intensity;

    //线性混合
    //mix: 返回线性混合的x和y，如：x⋅(1−a)+y⋅a
    vec3 r = mix(currentColor.rgb, blur.rgb, currentIntensity);

    gl_FragColor = vec4(r, 1.0);//gl_FragColor: rgb不支持 yuv
}