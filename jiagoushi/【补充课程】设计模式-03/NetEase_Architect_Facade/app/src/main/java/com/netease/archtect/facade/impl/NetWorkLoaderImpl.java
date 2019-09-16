package com.netease.archtect.facade.impl;

import com.netease.archtect.facade.thing.NetWorkLoader;

import java.io.InputStream;

public class NetWorkLoaderImpl implements NetWorkLoader {

    @Override
    public InputStream loaderImageFromNet(String url) {
        System.out.println("通过图片url，从网络加载图片");
        return null;
    }
}
