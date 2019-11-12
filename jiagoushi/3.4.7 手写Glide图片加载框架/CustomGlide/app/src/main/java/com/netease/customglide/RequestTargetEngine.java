package com.netease.customglide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.netease.customglide.cache.ActiveCache;
import com.netease.customglide.cache.MemoryCache;
import com.netease.customglide.cache.MemoryCacheCallback;
import com.netease.customglide.cache.disk.DiskLruCacheImpl;
import com.netease.customglide.fragment.LifecycleCallback;
import com.netease.customglide.load_data.LoadDataManager;
import com.netease.customglide.load_data.ResponseListener;
import com.netease.customglide.pool.LruBitmapPool;
import com.netease.customglide.resource.Key;
import com.netease.customglide.resource.Value;
import com.netease.customglide.resource.ValueCallback;

/**
 * 加载图片资源
 */
public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseListener {

    private final String TAG = RequestTargetEngine.class.getSimpleName();

    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经开启了 初始化了....");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经停止中 ....");
    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 进行释放操作 缓存策略释放操作等 >>>>>> ....");

        if (activeCache != null) {
            activeCache.closeThread(); // 把活动缓存给释放掉
        }

        // 把内存缓存移除
        // ....
    }

    private ActiveCache activeCache; // 活动缓存
    private MemoryCache memoryCache; // 内存缓存
    private DiskLruCacheImpl diskLruCache; // 磁盘缓存
    private LruBitmapPool lruBitmapPool;  // 复用池
    private final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this); // 回调告诉外界，Value资源不再使用了 设置监听
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE); //LRU最少使用的元素会被移除 设置监听
            memoryCache.setMemoryCacheCallback(this);
        }
        // 初始化磁盘缓存
        diskLruCache = new DiskLruCacheImpl();

        if (lruBitmapPool == null) {
            lruBitmapPool = new LruBitmapPool(MEMORY_MAX_SIZE);
        }
    }

    private String path;
    private Context glideContext;
    private String key; // ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668
    private ImageView imageView; // 显示的目标

    /**
     * RequestManager传递的值
     */
    public void loadValueInitAction(String path, Context glideContext) {
        this.path = path;
        this.glideContext = glideContext;
        key = new Key(path).getKey();
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;

        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();

        // TODO 加载资源 --》 缓存 ---》网络/SD/ 加载资源 成功后 --》资源 保存到缓存中 >>>
        Value value = cacheAction();
        if (null != value) {
            // 使用完成了 减一
            value.nonUseAction();
            // Log.d(TAG, "into: cacheAction Bitmap value.Bitmap:" + value.getmBitmap() + " key"+ value.getKey());
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    // TODO 加载资源 --》 缓存 ---》网络/SD/ 加载资源 成功后 --》资源 保存到缓存中 >>>
    private Value cacheAction() {
        // Log.d(TAG, "loadValueInitAction: key:" + key);

        // TODO 第一步，判断活动缓存是否有资源，如果有资源 就返回， 否则就继续往下找
        Value value = activeCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载是在(活动缓存)中获取的资源>>>");

            // 返回 代表 使用了一次 Value
            value.useAction(); // 使用了一次 加一
            return value;
        }

        // TODO 第二步，从内存缓存中去找，如果找到了，内存缓存中的元素 “移动” 到 活动缓存， 然后再返回
        value = memoryCache.get(key);
        if (null != value) {
            // 移动操作
            memoryCache.shoudonRemove(key); // 移除内存缓存
            activeCache.put(key, value); // 把内存缓存中的元素 加入到活动缓存中

            Log.d(TAG, "cacheAction: 本次加载是在(内存缓存)中获取的资源>>>");

            // 返回 代表 使用了一次 Value
            value.useAction(); // 使用了一次 加一
            return value;
        }

        // TODO 第三步，从磁盘缓存中去找，如果找到了，把磁盘缓存中的元素 加入到 活动缓存中
        value = diskLruCache.get(key, lruBitmapPool);
        if (null != value) {
            // 把磁盘缓存中的元素 --> 加入到活动缓存中
            activeCache.put(key, value);

            // 把磁盘缓存中的元素 --> 加入到内存缓存中
            // memoryCache.put(key, value);

            Log.d(TAG, "cacheAction: 本次加载是在(磁盘缓存)中获取的资源>>>");

            // 返回 代表 使用了一次 Value
            value.useAction(); // 使用了一次 加一
            return value;
        }

        // TODO 第四步，真正的去加载外部资源了， 去网络上加载/去SD本地上加载
        value = new LoadDataManager().loadResource(path, this, glideContext);
        if (value != null)
            return value;

        return null;
    }

    /**
     * 活动缓存间接的调用Value所发出的
     * 回调告诉外界，Value资源不再使用了
     * 监听的方法（Value不再使用了）
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        // 把活动缓存操作的Value资源 加入到 内存缓存
        if (key != null && value != null) {
            // Log.d(TAG, "valueNonUseListener: value.getmBitmap().isMutable():" + value.getmBitmap().isMutable());
            memoryCache.put(key, value);
        }
    }

    /**
     * 内存缓存发出的
     * LRU最少使用的元素会被移除
     * @param key
     * @param oldValue
     */
    @Override
    public void entryRemovedMemoryCache(String key, Value oldValue) {
        // 添加到复用池 ...... ，空留的功能点
        lruBitmapPool.put(oldValue.getmBitmap());
    }

    // 加载外部资源成功
    @Override
    public void responseSuccess(Value value) {
        if (null != value) {
            saveCahce(key, value);
            // Log.d(TAG, "responseSuccess: value.getmBitmap().isMutable() " + value.getmBitmap().isMutable());
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    // 加载外部资源失败
    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: 加载外部资源失败 e:" + e.getMessage());
    }

    /**
     * 保存到缓存中
     * @param key
     * @param value
     */
    private void saveCahce(String key, Value value) {
        Log.d(TAG, "saveCahce: >>>>>>>>>>>>>>>>>>>>>> 加载外部资源成功后，保存到缓存中 key:" + key + " value:" + value);
        value.setKey(key);

        if (diskLruCache != null) {
            diskLruCache.put(key, value); // 保存到磁盘缓存中
        }
    }
}
