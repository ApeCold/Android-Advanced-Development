package com.netease.hookproject;

import dalvik.system.DexClassLoader;

// 专门加载插件里面的class 用的加载器
public class PluginClassLoader extends DexClassLoader {

    public PluginClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
