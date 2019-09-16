package com.netease.archtect.facade.thing;

import android.graphics.Bitmap;

public interface DiskCache {

    // 内存中没有找到，从本地文件中寻找缓存图片
    Bitmap findByDisk(String url);
}
