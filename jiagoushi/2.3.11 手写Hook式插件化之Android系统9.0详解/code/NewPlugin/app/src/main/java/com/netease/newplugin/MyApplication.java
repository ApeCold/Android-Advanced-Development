package com.netease.newplugin;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Hook AMS 检查  偷梁换柱
        try {
            hookAMS();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO Hook 2 还原 ProxyActivity 给 换回来
        try {
            hookActivityThread();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // 倒序写代码，HOOK最有效的方式
    // 如果不用倒序，就是属于背代码
    private void hookAMS() throws Exception {

        /**
         * @1 怎么来(反射)， @1是什么 == IActivityManagerSingleton
         */
        Class mActivityManagerClass = Class.forName("android.app.ActivityManager");
        Field mIActivityManagerSingletonField = mActivityManagerClass.getDeclaredField("IActivityManagerSingleton");
        mIActivityManagerSingletonField.setAccessible(true);
        Object IActivityManagerSingleton = mIActivityManagerSingletonField.get(null);


        Class mSingletonClass = Class.forName("android.util.Singleton");

        Field mInstanceField = mSingletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true); // 让虚拟机不要去检测

        /**
         * @3 android.app.IActivityManager.aidl 9.0
         */
        Class mIActivityManagerClass = Class.forName("android.app.IActivityManager");

        /**
         * @4 怎么来  真实系统的IActivityManager  看源码...
         */
        final Object mIActivityManagerObj = mActivityManagerClass.getMethod("getService").invoke(null);

        /**
         * @2 是什么 ，由于我们要去监测 启动startActivity的行为，所以需要动态地代理
         */
        Object mIActivityManagerProxy = Proxy.newProxyInstance(getClassLoader(),
                new Class[]{mIActivityManagerClass}, // @3 怎么来？
                new InvocationHandler() { // 监听
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("startActivity".equals(method.getName())) {
                            // 下标2 == Intent（LoginActivity(无法过检查) 更换 代理Activity）
                            Intent proxyIntent = new Intent(MyApplication.this, ProxyActivity.class);

                            // 下表2 == Intent
                            // 优化
                            for (Object arg : args) {
                                // 作业 动态取出 Intent ，interfaceOf 判断类型
                            }

                            // 考虑到后面的还原，必须携带目标过去
                            proxyIntent.putExtra("targetIntent", ((Intent) args[2]));

                            args[2] = proxyIntent;
                        }

                        // 系统继续执行下去
                        return method.invoke(mIActivityManagerObj, args);
                    }
                });


        // 【第一步 替换】
        mInstanceField.set(IActivityManagerSingleton, mIActivityManagerProxy);

    }


    // 为什么要那public，Google工程师，会对public(工程师考虑)很少修改， 修改的是私有的
    private void hookActivityThread() throws Exception {
        /**
         * @1 是什么  mH
         */
        Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
        Field mhField = mActivityThreadClass.getDeclaredField("mH");
        mhField.setAccessible(true);

        /**
         * @3 ActivityThread
         */
        Object  mActivityThread = mActivityThreadClass.getMethod("currentActivityThread").invoke(null);
        mhField.get(mActivityThread);

        Object mH = mhField.get(mActivityThread);


        // 倒序 【第一步】 后面代码全部推理
        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        mCallbackField.set(mH, new MyCallback());
    }

    // Callback mCallback = 我们自己的;  @2
    private class MyCallback implements android.os.Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            // 开始源
            Object mClientTransaction = msg.obj;

            // 分析源码，
            /**
             * 拿到 Intent(ProxyActivity)
             *
             * final ClientTransaction transaction = (ClientTransaction) msg.obj;
             * mTransactionExecutor.execute(transaction);
             */

            try {
                Class mLaunchActivityItemClass = Class.forName("android.app.servertransaction.LaunchActivityItem");

                /**
                 * @1 是什么  LaunchActivityItem， 分析源码...
                 */
                // private List<ClientTransactionItem> mActivityCallbacks;
                Field mactivityCallbacks = mClientTransaction.getClass().getDeclaredField("mActivityCallbacks");
                mactivityCallbacks.setAccessible(true);
                List mActivityCallbacks = (List) mactivityCallbacks.get(mClientTransaction);
                if (mActivityCallbacks.size() == 0) {
                    return false;
                }

                Object mLaunchActivityItem = mActivityCallbacks.get(0);  //   不一定LaunchActivityItem    Window....      // 0 Activity的启动 一定第0个
                // 为什么要取出集合的  和 LaunchActivityItemClass类型做对比 ？
                // 看源码  ActivityThread 会添加 ActivityResultItem 我们要区分，不是ActivityResultItem extends ClientTransactionItem，  必须是LaunchActivityItem extends ClientTransaction
                if (mLaunchActivityItemClass.isInstance(mLaunchActivityItem) == false) {
                    return false;
                }

                Field mIntentField = mLaunchActivityItemClass.getDeclaredField("mIntent");
                mIntentField.setAccessible(true);

                /**
                 * @2 LaunchActivityItem Intent mIntent   ProxyActivity LoginActivity
                 */
                Intent proxyIntent = (Intent) mIntentField.get(mLaunchActivityItem);

                // 目标的Intent
                Intent targetIntent = proxyIntent.getParcelableExtra("targetIntent");
                if (targetIntent != null) {
                    mIntentField.setAccessible(true);
                    mIntentField.set(mLaunchActivityItem, targetIntent); // 替换【第一步】【倒序写代码】 第一步 换掉 Intent
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
