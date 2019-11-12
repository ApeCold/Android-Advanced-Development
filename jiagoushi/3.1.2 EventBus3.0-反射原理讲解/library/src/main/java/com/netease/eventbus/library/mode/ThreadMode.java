package com.netease.eventbus.library.mode;

/**
 * 线程状态
 */
public enum ThreadMode {

    // 事件的处理在和事件的发送在相同的进程，所以事件处理时间不应太长，不然影响事件的发送线程，而这个线程可能是UI线程
    POSTING,

    // 事件的处理会在UI线程中执行,事件处理不应太长时间
    MAIN,

    // 后台进程，处理如保存到数据库等操作
    BACKGROUND,

    // 异步执行，另起线程操作。事件处理会在单独的线程中执行，主要用于在后台线程中执行耗时操作
    ASYNC
}
