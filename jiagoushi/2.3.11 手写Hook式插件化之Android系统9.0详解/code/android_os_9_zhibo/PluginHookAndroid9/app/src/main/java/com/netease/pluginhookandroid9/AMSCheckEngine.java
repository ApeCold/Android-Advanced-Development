package com.netease.pluginhookandroid9;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 专门处理绕过AMS检测，让LoginActivity可以正常通过
 */
public class AMSCheckEngine {

    /**
     * TODO 同学们，注意：此方法 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
     * @param mContext
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static void mHookAMS(final Context mContext) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // 公共区域
        Object mIActivityManagerSingleton = null; // TODO 同学们，注意：公共区域 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
        Object mIActivityManager = null; // TODO 同学们，注意：公共区域 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本

        if (AndroidSdkVersion.isAndroidOS_26_27_28()) {
            // @3 的获取    系统的 IActivityManager.aidl
            Class mActivityManagerClass = Class.forName("android.app.ActivityManager");
            mIActivityManager = mActivityManagerClass.getMethod("getService").invoke(null);


            // @1 的获取    IActivityManagerSingleton
            Field mIActivityManagerSingletonField = mActivityManagerClass.getDeclaredField("IActivityManagerSingleton");
            mIActivityManagerSingletonField.setAccessible(true);
            mIActivityManagerSingleton = mIActivityManagerSingletonField.get(null);

        } else if (AndroidSdkVersion.isAndroidOS_21_22_23_24_25()) {
            // @3 的获取
            Class mActivityManagerClass = Class.forName("android.app.ActivityManagerNative");
            Method getDefaultMethod = mActivityManagerClass.getDeclaredMethod("getDefault");
            getDefaultMethod.setAccessible(true);
            mIActivityManager = getDefaultMethod.invoke(null);

            // @1 的获取 gDefault
            Field gDefaultField = mActivityManagerClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            mIActivityManagerSingleton = gDefaultField.get(null);
        }

        // @2 的获取    动态代理
        Class mIActivityManagerClass = Class.forName("android.app.IActivityManager");
        final Object finalMIActivityManager = mIActivityManager;
        Object mIActivityManagerProxy =  Proxy.newProxyInstance(mContext.getClassLoader(),
                new Class[]{mIActivityManagerClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("startActivity".equals(method.getName())) {
                            // 把LoginActivity 换成 ProxyActivity
                            // TODO 狸猫换太子，把不能经过检测的LoginActivity 替换 成能够经过检测的ProxyActivity
                            Intent proxyIntent = new Intent(mContext, ProxyActivity.class);

                            // 把目标的LoginActivity 取出来 携带过去
                            Intent target = (Intent) args[2];
                            proxyIntent.putExtra(Parameter.TARGET_INTENT, target);
                            args[2] = proxyIntent;
                        }

                        // @3
                        return method.invoke(finalMIActivityManager, args);
                    }
                });

        if (mIActivityManagerSingleton == null || mIActivityManagerProxy == null) {
            throw new IllegalStateException("实在是没有检测到这种系统，需要对这种系统单独处理..."); // 10.0
        }

        Class mSingletonClass = Class.forName("android.util.Singleton");

        Field mInstanceField = mSingletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        // 把系统里面的 IActivityManager 换成 我们自己写的动态代理 【第一步】
        //  @1    @2
        mInstanceField.set(mIActivityManagerSingleton, mIActivityManagerProxy);
    }

}
