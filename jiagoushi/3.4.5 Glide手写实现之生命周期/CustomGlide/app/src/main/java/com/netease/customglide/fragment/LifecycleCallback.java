package com.netease.customglide.fragment;

public interface LifecycleCallback {

    // 生命周期初始化了
    public void glideInitAction();

    // 生命周期 停止了
    public void glideStopAction();

    // 生命周期 释放 操作了
    public void glideRecycleAction();

}
