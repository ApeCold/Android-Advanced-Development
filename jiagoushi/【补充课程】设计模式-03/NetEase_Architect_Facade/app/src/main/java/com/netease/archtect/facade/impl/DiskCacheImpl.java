package com.netease.archtect.facade.impl;

import android.graphics.Bitmap;

import com.netease.archtect.facade.thing.DiskCache;

public class DiskCacheImpl implements DiskCache {

    @Override
    public Bitmap findByDisk(String url) {
        System.out.println("通过图片url，寻找本地文件中缓存图片");
        return null;
    }
}
