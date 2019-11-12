package com.netease.customglide.pool;

import android.graphics.Bitmap;

// Bitmap 标准
public interface BitmapPool {

    /**
     * 加入到Bitmap内存复用池
     * @param bitmap
     */
    void put(Bitmap bitmap);

    /**
     * 从Bitmap内存复用池里面取出来
     * @param width
     * @param height
     * @param config
     * @return
     */
    Bitmap get(int width, int height, Bitmap.Config config);

}
