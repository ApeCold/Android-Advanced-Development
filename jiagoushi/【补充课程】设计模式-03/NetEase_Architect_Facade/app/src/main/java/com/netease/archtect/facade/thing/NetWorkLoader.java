package com.netease.archtect.facade.thing;

import java.io.InputStream;

public interface NetWorkLoader {

    // 内存中没找到，本地文件中没找到，开始从网络加载图片
    InputStream loaderImageFromNet(String url);
}
