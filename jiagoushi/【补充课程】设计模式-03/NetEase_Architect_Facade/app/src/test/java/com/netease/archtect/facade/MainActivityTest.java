package com.netease.archtect.facade;

import com.netease.archtect.facade.impl.DiskCacheImpl;
import com.netease.archtect.facade.impl.MemoryCacheImpl;
import com.netease.archtect.facade.impl.NetWorkLoaderImpl;
import com.netease.archtect.facade.thing.DiskCache;
import com.netease.archtect.facade.thing.MemoryCache;
import com.netease.archtect.facade.thing.NetWorkLoader;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    private final static String URL = "http://www.163.com/logo.jpg";

    @Test
    public void onCreate() {
        // 常规的写法：
//        MemoryCache memoryCache = new MemoryCacheImpl();
//        memoryCache.findByMemory(URL);
//
//        DiskCache diskCache = new DiskCacheImpl();
//        diskCache.findByDisk(URL);
//
//        NetWorkLoader netWorkLoader = new NetWorkLoaderImpl();
//        netWorkLoader.loaderImageFromNet(URL);

        // 外观模式
        Facade facade = new Facade(URL);
        facade.loader();
    }
}