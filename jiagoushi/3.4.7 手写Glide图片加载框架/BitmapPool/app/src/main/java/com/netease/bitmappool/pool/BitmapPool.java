package com.netease.bitmappool.pool;

import android.graphics.Bitmap;

// 复用池 标准
public interface BitmapPool {

    /**
     * 存入到复用池
     * @param bitmap
     */
    void put(Bitmap bitmap);


    /**
     * 获取匹配可用复用的Bitmap
     * @param width
     * @param height
     * @param config
     * @return
     */
    Bitmap get(int width, int height, Bitmap.Config config);

}
