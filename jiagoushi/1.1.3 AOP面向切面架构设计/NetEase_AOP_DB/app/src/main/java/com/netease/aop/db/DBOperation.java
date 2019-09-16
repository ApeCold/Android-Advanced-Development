package com.netease.aop.db;

public interface DBOperation {

    void insert();

    void delete();

    void update();

    // 数据备份
    void save();
}
