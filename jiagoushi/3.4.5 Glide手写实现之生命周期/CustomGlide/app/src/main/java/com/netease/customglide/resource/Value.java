package com.netease.customglide.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.netease.customglide.Tool;

/**
 * 对Bitmap的封装
 */
public class Value {

    private final String TAG = Value.class.getSimpleName();

    // 单利模式
    private static Value value;

    public static Value getInstance() {
        if (null == value) {
            synchronized (Value.class) {
                if (null == value) {
                    value = new Value();
                }
            }
        }
        return value;
    }

    private Bitmap mBitmap;

    // 使用计数
    private int count;

    // 监听
    private ValueCallback callback;

    // 定义key
    private String key;

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    /**
     * TODO 使用一次 就 加一
     */
    public void useAction() {
        Tool.checkNotEmpty(mBitmap);

        if (mBitmap.isRecycled()) { // 已经被回收了
            Log.d(TAG, "useAction: 已经被回收了");
            return;
        }
        Log.d(TAG, "useAction: 加一 count:" + count);

        count ++;
    }

    /**
     * TODO 使用完成（不使用） 就 减一
     * count -- <= 0  不再使用了
     */
    public void nonUseAction() {
        if (count -- <= 0 && callback != null) {
            // 回调告诉外界，不再使用了
            callback.valueNonUseListener(key, this);
        }
        Log.d(TAG, "useAction: 减一 count:" + count);
    }

    /**
     * TODO 释放
     */
    public void recycleBitmap() {
        if (count > 0) {
            Log.d(TAG, "recycleBitmap: 引用计数大于0，证明还在使用中，不能去释放...");
            return;
        }

        if (mBitmap.isRecycled()) { // 被回收了
            Log.d(TAG, "recycleBitmap: mBitmap.isRecycled() 已经被释放了...");
            return;
        }

        mBitmap.recycle();

        value = null;

        System.gc();
    }
}
