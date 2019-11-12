package com.netease.javapoet.test;

import com.netease.javapoet.MainActivity;

/**
 * 模拟APT生成后的文件样子
 */
public class XActivity$$ARouter {

    public static Class<?> findTargetClass(String path) {
//        if (path.equals("/app/MainActivity")) {
//            return MainActivity.class;
//        }
//        return null;

        return path.equals("/app/MainActivity") ? MainActivity.class : null;
    }
}
