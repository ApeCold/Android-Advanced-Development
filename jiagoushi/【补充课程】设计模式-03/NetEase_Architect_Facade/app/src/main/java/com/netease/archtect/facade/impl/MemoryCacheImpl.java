package com.netease.archtect.facade.impl;

import android.graphics.Bitmap;

import com.netease.archtect.facade.thing.MemoryCache;

public class MemoryCacheImpl implements MemoryCache {

    @Override
    public Bitmap findByMemory(String url) {
        System.out.println("通过图片url，寻找内存中缓存图片");
        return null;
    }
}
