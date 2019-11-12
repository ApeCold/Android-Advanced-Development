package com.netease.bitmappool.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

public class BitmapPoolImpl extends LruCache<Integer, Bitmap> implements BitmapPool {

    private final String TAG = BitmapPoolImpl.class.getSimpleName();

    // 为了筛选出 合适的 Bitmap 容器
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    // 存入复用池
    @Override
    public void put(Bitmap bitmap) {

        // todo 条件一 bitmap.isMutable() == true;
        if (!bitmap.isMutable()) {
            Log.d(TAG, "put: 条件一 bitmap.isMutable() == true 不满足，不能存入复用池..");
            return;
        }

        // todo 条件二
        // 计算Bitmap的大小
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize >= maxSize()) {
            Log.d(TAG, "put: 条件二 大于了maxSize 不满足，不能存入复用池..");
            return;
        }

        // todo bitmap 存入 LruCache
        put(bitmapSize, bitmap);

        // 存入 筛选 容器
        treeMap.put(bitmapSize, null); // 10000

        Log.d(TAG, "put: 添加到复用池...");
    }

    // 获取可用复用的Bitmap
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {

        /**
         * ALPHA_8  理论上 实际上Android自动做处理的 只有透明度 8位  1个字节
         * w*h*1
         *
         * RGB_565  理论上 实际上Android自动做处理的  R red红色 5， G绿色 6， B蓝色 5   16位 2个字节 没有透明度
         * w*h*2
         *
         * ARGB_4444 理论上 实际上Android自动做处理 A透明度 4位  R red红色4位   16位 2个字节
         *
         * 质量最高的：
         * ARGB_8888 理论上 实际上Android自动做处理  A 8位 1个字节  ，R 8位 1个字节， G 8位 1个字节， B 8位 1个字节
         *
         * 常用的 ARGB_8888  RGB_565
         */
        // 常用的 4==ARGB_8888  2==RGB_565
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);

        Integer key = treeMap.ceilingKey(getSize); // 可以查找到容器里面 和getSize一样大的，也可以 比getSize还要大的
        // 如果treeMap 还没有put，那么一定是 null
        if (key == null) {
            return null; // 没有找到合适的 可以复用的 key
        }

        // key == 10000     getSize==12000

        // 查找容器取出来的key，必须小于 计算出来的 (getSize * 2 ： )
        // if (key <= (getSize * 2)) {
            Bitmap remove = remove(key);// 复用池 如果要取出来，肯定要移除，不想给其他地方用了
            Log.d(TAG, "get: 从复用池 里面获取了Bitmap...");
            return remove;
        // }
        // return null;
    }

    /**
     * 计算Bitmap的大小
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap bitmap) {
        // 最早期的时候 getRowBytes() * getHeight();

        // Android 3.0 12 API  bitmap.getByteCount()
        // bitmap.getByteCount()

        // Android 4.4 19 API 以后的版本
        // bitmap.getAllocationByteCount();

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    // 元素大小
    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        // return super.sizeOf(key, value);
        return getBitmapSize(value);
    }

    // 元素被移除
    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}
