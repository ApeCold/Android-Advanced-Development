package com.netease.archtect.facade.thing;

import android.graphics.Bitmap;

public interface MemoryCache {

    // 从内存中寻找缓存图片
    Bitmap findByMemory(String url);
}
