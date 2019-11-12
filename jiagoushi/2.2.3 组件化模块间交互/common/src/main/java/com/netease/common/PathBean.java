package com.netease.common;

/**
 * 路径对象（公共基础库中，所有子模块都可以调用）
 * 如：
 * path : ”order/Order_MainActivity”
 * clazz : Order_MainActivity.class
 */
public class PathBean {

    private String path;
    private Class clazz;

    public PathBean() {
    }

    public PathBean(String path, Class clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
