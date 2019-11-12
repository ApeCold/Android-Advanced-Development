//float数据是什么精度的
precision mediump float;

//采样点的坐标
varying vec2 aCoord;

//采样器
uniform sampler2D vTexture;
uniform vec2 left_eye;//左眼坐标
uniform vec2 right_eye;//右眼坐标

//用代码翻译算法公式
//r: 原来的点距离眼睛中心点的距离
//rmax：放大区域的半径
float fs(float r, float rmax){
    float a = 0.4;//放大系数
    return (1.0 - pow(r / rmax - 1.0, 2.0) * a) * r;//pow 内置函数
}

//在放大区域内，找眼睛中的某个点的像素来替代（复制眼睛里的像素到要放大的区域）

//计算新的点
//coord: 原来的点
//eye ： 眼睛坐标
//rmax：放大区域半径
vec2 calcNewCoord(vec2 oldCoord, vec2 eye, float rmax){
    vec2 newCoord = oldCoord;
    float r =  distance(oldCoord, eye);
    //在区域范围内才进行放大处理
//    if(r < rmax){// TODO 1: r不能为 0
    if(r > 0.0f && r < rmax){
        float fsr = fs(r, rmax);
//      （新的点 - 眼睛） /  （旧的点 - 眼睛） = 新的距离 / 旧的距离
//        (newCoord - eye) / (oldCoord - eye) = fsr / r
        newCoord = (fsr / r) * (oldCoord - eye) + eye;
    }
    return newCoord;
}

void main(){
    //变量 接收像素值
    // texture2D：采样器 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
    //gl_FragColor = texture2D(vTexture, aCoord);
    //根据两眼间距获取放大区域的半径 rmax
    float rmax = distance(left_eye, right_eye) / 2.0;
    vec2 newCoord = calcNewCoord(aCoord, left_eye, rmax);
    // TODO 2: calcNewCoord第一个参数应该是 newCoord, 这里相当于只判断了右眼
//    newCoord = calcNewCoord(aCoord, right_eye, rmax);
    newCoord = calcNewCoord(newCoord, right_eye, rmax);
    gl_FragColor = texture2D(vTexture, newCoord);
}