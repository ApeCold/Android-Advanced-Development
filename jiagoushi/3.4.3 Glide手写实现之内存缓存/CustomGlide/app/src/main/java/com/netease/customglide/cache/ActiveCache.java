package com.netease.customglide.cache;

import com.netease.customglide.Tool;
import com.netease.customglide.resource.Key;
import com.netease.customglide.resource.Value;
import com.netease.customglide.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动缓存 -- 真正被使用的资源
 */
public class ActiveCache {

    // 容器
    private Map<String, WeakReference<Value>> mapList = new HashMap<>();
    private ReferenceQueue<Value> queue; // 目的：为了监听这个弱引用 是否被回收了
    private boolean isCloseThread;
    private Thread thread;
    private boolean isShoudonRemove;
    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * TODO 添加 活动缓存
     * @param key
     * @param value
     */
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);

        // 绑定Value的监听 --> Value发起来的（Value没有被使用了，就会发起这个监听，给外界业务需要来使用）
        value.setCallback(valueCallback);

        // 存储 --》 容器
        mapList.put(key, new CustomoWeakReference(value, getQueue(), key));
    }

    /**
     * TODO 给外界获取Value
     * @param key
     * @return
     */
    public Value get(String key) {
        WeakReference<Value> valueWeakReference = mapList.get(key);
        if (null != valueWeakReference) {
            return valueWeakReference.get(); // 返回Value
        }
        return null;
    }

    /**
     * TODO 手动移除
     * @param key
     * @return
     */
    public Value remove(String key) {
        isShoudonRemove = true;
        WeakReference<Value> valueWeakReference = mapList.remove(key);
        isShoudonRemove = false; // 还原 目的是为了 让 GC自动移除 继续工作
        if (null != valueWeakReference) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * TODO 释放 关闭线程
     */
    public void closeThread() {
        isCloseThread = true;
        /*if (null != thread) {
            thread.interrupt(); // 中断线程
            try {
                thread.join(TimeUnit.SECONDS.toMillis(5)); // 线程稳定安全 停止下来
                if (thread.isAlive()) { // 证明线程还是没有结束
                    throw new IllegalStateException("活动缓存中 关闭线程 线程没有停止下来...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        mapList.clear();

        System.gc();
    }

    /**
     * 监听弱引用 成为弱引用的子类  为什么要成为弱引用的子类（目的：为了监听这个弱引用 是否被回收了）
     */
    public class CustomoWeakReference extends WeakReference<Value> {

        private String key;

        public CustomoWeakReference(Value referent, ReferenceQueue<? super Value> queue, String key) {
            super(referent, queue);
            this.key = key;
        }
    }

    /**
     * 为了监听 弱引用被回收，被动移除的
     * @return
     */
    private ReferenceQueue<Value> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();

            // 监听这个弱引用 是否被回收了
            thread =  new Thread(){
                @Override
                public void run() {
                    super.run();

                    while (!isCloseThread) {

                        try {
                            if (!isShoudonRemove) {
                                // queue.remove(); 阻塞式的方法

                                Reference<? extends Value> remove = queue.remove(); // 如果已经被回收了，就会执行到这个方法
                                CustomoWeakReference weakReference = (CustomoWeakReference) remove;
                                // 移除容器     !isShoudonRemove：为了区分手动移除 和 被动移除
                                if (mapList != null && !mapList.isEmpty()) {
                                    mapList.remove(weakReference.key);
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            };
            thread.start();
        }
        return queue;
    }
}
