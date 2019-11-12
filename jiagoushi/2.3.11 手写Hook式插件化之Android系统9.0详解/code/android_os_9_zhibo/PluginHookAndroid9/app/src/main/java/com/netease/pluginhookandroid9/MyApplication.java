package com.netease.pluginhookandroid9;

import android.app.Application;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 宿主的Application
 */
public class MyApplication extends Application {

    public static String pluginPath;

    DexElementFuse dexElementFuse;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO 同学们只需要知道，下面这行代码，可以拿到 插件的路径
        pluginPath = ApkCopyAssetsToDir.copyAssetToCache(this, Parameter.PLUGIN_FILE_NAME);

        // TODO 第一个HOOK 同学们 具体作用，看里面的方法描述哦
        try {
            AMSCheckEngine.mHookAMS(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // TODO 第二个HOOK 同学们 具体作用，看里面的方法描述哦
        try {
            ActivityThreadmHRestore.mActivityThreadmHAction(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO 进行融合
        dexElementFuse = new DexElementFuse();
        try {
            dexElementFuse.mainPluginFuse(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写这个资源管理器
     * 为什么要去重写资源管理器呢？
     *  答：是为了提供资源的加载工作
     * 那么为什么要在Application里面重写资源管理器呢？
     *  答：虽然每一个Activity都有资源管理器，但是不可能每一个Activity都去重写，所以只需要在Application里面重写就好了
     *     Application的getResources 是所有Activity共用的
     * @return
     */
    @Override
    public Resources getResources() {
        return dexElementFuse.getResources() == null ? super.getResources() : dexElementFuse.getResources();
    }
}
