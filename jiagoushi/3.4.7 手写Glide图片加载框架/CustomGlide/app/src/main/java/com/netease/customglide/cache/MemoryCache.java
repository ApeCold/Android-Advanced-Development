package com.netease.customglide.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.netease.customglide.resource.Value;

/**
 * 内存缓存--LRU算法
 */
public class MemoryCache extends LruCache<String, Value> {

    private boolean shoudonRemove;

    // TODO 手动移除
    public Value shoudonRemove(String key) {
        shoudonRemove = true;
        Value value = remove(key);
        shoudonRemove = false;  // !shoudonRemove == 被动的
        return value;
    }

    // put get

    private MemoryCacheCallback memoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * 传入元素最大值，给LruCache
     * @param maxSize
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(@NonNull String key, @NonNull Value value) {
        // return super.sizeOf(key, value);
        Bitmap bitmap = value.getmBitmap(); // 8

        // 最开始的时候
        // int result = bitmap.getRowBytes() * bitmap.getHeight();

        // API 12  3.0
        // result = bitmap.getByteCount(); // 在bitmap内存复用上有区别 （所属的）

        // API 19 4.4
        // result = bitmap.getAllocationByteCount(); // 在bitmap内存复用上有区别 （整个的）

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }

        return bitmap.getByteCount();
    }

    /**
     * 1.重复的key
     * 2.最少使用的元素会被移除
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Value oldValue, @Nullable Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);

        if (memoryCacheCallback != null && !shoudonRemove) { // !shoudonRemove == 被动的
            memoryCacheCallback.entryRemovedMemoryCache(key, oldValue);
        }

    }
}
