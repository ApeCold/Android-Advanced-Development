package com.netease.archtect.facade;

import com.netease.archtect.facade.impl.DiskCacheImpl;
import com.netease.archtect.facade.impl.MemoryCacheImpl;
import com.netease.archtect.facade.impl.NetWorkLoaderImpl;
import com.netease.archtect.facade.thing.DiskCache;
import com.netease.archtect.facade.thing.MemoryCache;
import com.netease.archtect.facade.thing.NetWorkLoader;

public class Facade {

    private String url;
    private MemoryCache memoryCache;
    private DiskCache diskCache;
    private NetWorkLoader netWorkLoader;

    public Facade(String url) {
        this.url = url;
        memoryCache = new MemoryCacheImpl();
        diskCache = new DiskCacheImpl();
        netWorkLoader = new NetWorkLoaderImpl();
    }

    void loader() {
        memoryCache.findByMemory(url);
        diskCache.findByDisk(url);
        netWorkLoader.loaderImageFromNet(url);
    }
}
