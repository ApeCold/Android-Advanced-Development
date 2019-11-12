package com.netease.apt.test;

import com.netease.apt.MainActivity;

/**
 * 模拟APT生成后的文件样子
 */
public class XActivity$$ARouter {

    public static Class<?> findTargetClass(String path) {
        if (path.equals("/app/MainActivity")) {
            return MainActivity.class;
        }
        return null;
    }
}
