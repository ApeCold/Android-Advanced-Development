package com.netease.customglide.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {

    private final String TAG = LruBitmapPool.class.getSimpleName();

    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    public void put(Bitmap bitmap) {
        // todo 复用的条件1 Bitmap
        if (!bitmap.isMutable()) {
            if (bitmap.isRecycled() == false) {
                bitmap.recycle();
            }
            Log.d(TAG, "put: 复用的条件1 Bitmap.ismutable 是false，条件不满足，不能复用 添加..." + bitmap);
            return;
        }

        // todo 复用的条件2 如果添加复用的Bitmap大小，大于LRU MaxSize 就不复用
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize > maxSize()) {
            if (bitmap.isRecycled() == false) {
                bitmap.recycle();
            }
            Log.d(TAG, "put: 复用的条件2 Bitmap.Size大于LruMaxSize，条件不满足，不能复用 添加...");
            return;
        }

        // 添加到 LRU Cahce中去
        put(bitmapSize, bitmap);

        // 保存到 TreeMap 是为了筛选
        treeMap.put(bitmapSize, null);

        Log.d(TAG, "put: 添加到复用池了....");
    }

    /**
     * 获得Bitmap的大小
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap bitmap) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else {
            return bitmap.getByteCount();
        }
    }

    /**
     *
     * @param width
     * @param height
     * @param config
     * @return
     */
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        // 我们这里新获取的Bitmap内存大小，计算方式是 只管 ARGB_8888  RGB_565
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        Integer key = treeMap.ceilingKey(getSize);// 获得 getSize这么大的key，同时还可以获得 比 getSize还要大的key
        if (key == null) {
            return null; // 如果找不到 保存的key，就直接返回null，无法复用
        }
        // 找出来的key 小于等于 （getSize * 2）
        if (key <= (getSize * 2)) {
            Bitmap resultBitmap = remove(key);
            Log.d(TAG, "get: 从复用池获取:" + resultBitmap);
            return resultBitmap;
        }
        return null;
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        //  return super.sizeOf(key, value);
        return getBitmapSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        // super.entryRemoved(evicted, key, oldValue, newValue);
        // 把treeMap 里面的给移除
        treeMap.remove(key);
    }
}
