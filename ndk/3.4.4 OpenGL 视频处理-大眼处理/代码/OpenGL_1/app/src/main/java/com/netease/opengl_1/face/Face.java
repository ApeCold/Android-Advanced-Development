package com.netease.opengl_1.face;

import java.util.Arrays;

/**
 * 人脸javabean
 */
public class Face {

    /**
     * 人脸框（矩形框）：左上角坐标 + 宽 + 高
     * 关键点坐标：SeetaFaceEngine （Github中科院开源的），最新2.0: SeetaFaceEngine2
     * 5个：左眼，右眼，鼻尖，左嘴角，右嘴角
     * 0，1 ： 人脸框的 x, y
     * 2：左眼 x
     * 3：左眼 y
     * 4：右眼 x
     * 5：右眼 y
     */
    public float[] landmarks;
    // 人脸框的宽
    public int width;
    // 人脸框的高
    public int height;
    // 检测人脸图片的宽
    public int imgWidth;
    // 检测人脸图片的高
    public int imgHeight;

    Face(int width, int height, int imgWidth,int imgHeight, float[] landmarks) {
        this.width = width;
        this.height = height;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.landmarks = landmarks;
    }

    @Override
    public String toString() {
        return "Face{" +
                "landmarks=" + Arrays.toString(landmarks) +
                ", width=" + width +
                ", height=" + height +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}';
    }
}
