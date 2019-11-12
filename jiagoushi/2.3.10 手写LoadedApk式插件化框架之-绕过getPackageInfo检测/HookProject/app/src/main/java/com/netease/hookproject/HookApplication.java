package com.netease.hookproject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class HookApplication extends Application {

    // 增加权限的管理
    private static List<String> activityList = new ArrayList<>();

    static {
        activityList.add(TestActivity.class.getName()); // 有权限
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            hookAmsAction();
        } catch (Exception e) {
            e.printStackTrace();

            Log.d("hook", "hookAmsAction 失败 e:" + e.toString());
        }

        try {
            hookLuanchActivity();
        } catch (Exception e) {
            e.printStackTrace();

            Log.d("hook", "hookLuanchActivity 失败 e:" + e.toString());
        }


// 不使用融合的方式
//        try {
//            pluginToAppAction();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            Log.d("hook", "pluginToAppAction 失败 e:" + e.toString());
//        }


        try {
            customLoadedApkAction();
        } catch (Exception e) {
            e.printStackTrace();

            Log.d("hook", " 失败 e:" + e.toString());
        }
    }



    /**
     * 要在执行 AMS之前，把TestActivity 替换可用的 ProxyActivity，替换在AndroidManifest里面配置的Activity
     */
    private void hookAmsAction() throws Exception {

        // 动态代理
        Class mIActivityManagerClass = Class.forName("android.app.IActivityManager");

        // 我们要拿到IActivityManager对象，才能让动态代理里面的 invoke 正常执行下
        // 执行此方法 static public IActivityManager getDefault()，就能拿到 IActivityManager
        Class mActivityManagerNativeClass2 = Class.forName("android.app.ActivityManagerNative");
        final Object mIActivityManager = mActivityManagerNativeClass2.getMethod("getDefault").invoke(null);

        // 本质是IActivityManager
        Object mIActivityManagerProxy = Proxy.newProxyInstance(

                HookApplication.class.getClassLoader(),

                new Class[]{mIActivityManagerClass}, // 要监听的接口

                new InvocationHandler() { // IActivityManager 接口的回调方法

                    /**
                     * @param proxy
                     * @param method IActivityManager里面的方法
                     * @param args IActivityManager里面的参数
                     * @return
                     * @throws
                     */

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if ("startActivity".equals(method.getName())) {
                            // 做自己的业务逻辑
                            // 换成 可以 通过 AMS检查的 ProxyActivity

                            // 用ProxyActivity 绕过了 AMS检查
                            Intent intent = new Intent(HookApplication.this, ProxyActivity.class);
                            intent.putExtra("actionIntent", ((Intent) args[2])); // 把之前TestActivity保存 携带过去
                            args[2] = intent;
                        }

                        Log.d("hook", "拦截到了IActivityManager里面的方法" + method.getName());

                        // 让系统继续正常往下执行
                        return method.invoke(mIActivityManager, args);
                    }
                });

        /**
         * 为了拿到 gDefault
         * 通过 ActivityManagerNative 拿到 gDefault变量(对象)
         */
        Class mActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
        Field gDefaultField = mActivityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true); // 授权
        Object gDefault = gDefaultField.get(null);


        // 替换点
        Class mSingletonClass = Class.forName("android.util.Singleton");
        // 获取此字段 mInstance
        Field mInstanceField = mSingletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true); // 让虚拟机不要检测 权限修饰符
        // 替换
        mInstanceField.set(gDefault, mIActivityManagerProxy); // 替换是需要gDefault
    }


    /**
     * Hook LuanchActivity,即将要实例化Activity，要把ProxyActivity 给 换回来 ---》 TestActivity
     */
    private void hookLuanchActivity() throws Exception {

        Field mCallbackFiled = Handler.class.getDeclaredField("mCallback");
        mCallbackFiled.setAccessible(true); // 授权

        /**
         * handler对象怎么来
         * 1.寻找H，先寻找ActivityThread
         *
         * 执行此方法 public static ActivityThread currentActivityThread()
         *
         * 通过ActivityThread 找到 H
         *
         */
        Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
        // 获得ActivityThrea对象
        Object mActivityThread = mActivityThreadClass.getMethod("currentActivityThread").invoke(null);

        Field mHField = mActivityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        // 获取真正对象
        Handler mH = (Handler) mHField.get(mActivityThread);

        mCallbackFiled.set(mH, new MyCallback(mH)); // 替换 增加我们自己的实现代码
    }

    public static final int LAUNCH_ACTIVITY         = 100;

    class MyCallback implements Handler.Callback {

        private Handler mH;

        public MyCallback(Handler mH) {
            this.mH = mH;
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {

                case LAUNCH_ACTIVITY:
                    // 做我们在自己的业务逻辑（把ProxyActivity 换成  TestActivity）
                    Object obj = msg.obj; // 本质 ActivityClientRecord

                    try {
                        // 我们要获取之前Hook携带过来的 TestActivity
                        Field intentField = obj.getClass().getDeclaredField("intent");
                        intentField.setAccessible(true);

                        // 获取 intent 对象，才能取出携带过来的 actionIntent
                        Intent intent = (Intent) intentField.get(obj);
                        // actionIntent == TestActivity的Intent
                        Intent actionIntent = intent.getParcelableExtra("actionIntent");

                        if (actionIntent != null) {
                            /*
                            if (activityList.contains(actionIntent.getComponent().getClassName())) {
                                intentField.set(obj, actionIntent); // 把ProxyActivity 换成  TestActivity
                            } else { // 没有权限
                                intentField.set(obj, new Intent(HookApplication.this, PermissionActivity.class));
                            }
                            */

                            intentField.set(obj, actionIntent); // 把ProxyActivity 换成  TestActivity


                            /***
                             *  我们在以下代码中，对插件  和 宿主 进行区分
                             */
                            Field activityInfoField = obj.getClass().getDeclaredField("activityInfo");
                            activityInfoField.setAccessible(true); //授权
                            ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);

                            // 什么时候 加载插件的  ？
                            if (actionIntent.getPackage() == null) { // 证明是插件
                                activityInfo.applicationInfo.packageName = actionIntent.getComponent().getPackageName();

                                // Hook 拦截此 getPackageInfo 做自己的逻辑
                                hookGetPackageInfo();

                            } else { // 宿主
                                activityInfo.applicationInfo.packageName = actionIntent.getPackage();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }


            mH.handleMessage(msg);
            // 让系统继续正常往下执行
            // return false; // 系统就会往下执行
            return true; // 系统不会往下执行
        }
    }


    /**
     * 把插件的dexElements 和 宿主中的 dexElements 融为一体
     */
    private void pluginToAppAction() throws Exception {
        // 第一步：找到宿主 dexElements 得到此对象   PathClassLoader代表是宿主
        PathClassLoader pathClassLoader = (PathClassLoader) this.getClassLoader(); // 本质就是PathClassLoader
        Class mBaseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
        // private final DexPathList pathList;
        Field pathListField = mBaseDexClassLoaderClass.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object mDexPathList = pathListField.get(pathClassLoader);

        Field dexElementsField = mDexPathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        // 本质就是 Element[] dexElements
        Object dexElements = dexElementsField.get(mDexPathList);

        /*** ---------------------- ***/


        // 第二步：找到插件 dexElements 得到此对象，代表插件 DexClassLoader--代表插件
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "p.apk");
        if (!file.exists()) {
            throw new FileNotFoundException("没有找到插件包!!");
        }
        String pluginPath = file.getAbsolutePath();
        File fileDir = this.getDir("pluginDir", Context.MODE_PRIVATE); // data/data/包名/pluginDir/
        DexClassLoader dexClassLoader = new
                DexClassLoader(pluginPath, fileDir.getAbsolutePath(), null, getClassLoader());

        Class mBaseDexClassLoaderClassPlugin = Class.forName("dalvik.system.BaseDexClassLoader");
        // private final DexPathList pathList;
        Field pathListFieldPlugin = mBaseDexClassLoaderClassPlugin.getDeclaredField("pathList");
        pathListFieldPlugin.setAccessible(true);
        Object mDexPathListPlugin = pathListFieldPlugin.get(dexClassLoader);

        Field dexElementsFieldPlugin = mDexPathListPlugin.getClass().getDeclaredField("dexElements");
        dexElementsFieldPlugin.setAccessible(true);
        // 本质就是 Element[] dexElements
        Object dexElementsPlugin = dexElementsFieldPlugin.get(mDexPathListPlugin);


        // 第三步：创建出 新的 dexElements []
        int mainDexLeng =  Array.getLength(dexElements);
        int pluginDexLeng =  Array.getLength(dexElementsPlugin);
        int sumDexLeng = mainDexLeng + pluginDexLeng;

        // 参数一：int[]  String[] ...  我们需要Element[]
        // 参数二：数组对象的长度
        // 本质就是 Element[] newDexElements
        Object newDexElements = Array.newInstance(dexElements.getClass().getComponentType(),sumDexLeng); // 创建数组对象


        // 第四步：宿主dexElements + 插件dexElements =----> 融合  新的 newDexElements
        for (int i = 0; i < sumDexLeng; i++) {
            // 先融合宿主
            if (i < mainDexLeng) {
                // 参数一：新要融合的容器 -- newDexElements
                Array.set(newDexElements, i, Array.get(dexElements, i));
            } else { // 再融合插件的
                Array.set(newDexElements, i, Array.get(dexElementsPlugin, i - mainDexLeng));
            }

        }

        // 第五步：把新的 newDexElements，设置到宿主中去
        // 宿主
        dexElementsField.set(mDexPathList, newDexElements);



        // 处理加载插件中的布局
        doPluginLayoutLoad();
    }

    private Resources resources;
    private AssetManager assetManager;

    /**
     * 处理加载插件中的布局
     * Resources
     */
    private void  doPluginLayoutLoad() throws Exception {
        assetManager = AssetManager.class.newInstance();

        // 把插件的路径 给 AssetManager
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "p.apk");
        if (!file.exists()) {
            throw new FileNotFoundException("没有找到插件包!!");
        }

        // 执行此 public final int addAssetPath(String path) 方法，才能把插件的路径添加进去
        Method method = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class); // 类类型
        method.setAccessible(true);
        method.invoke(assetManager, file.getAbsolutePath());

        Resources r = getResources(); // 拿到的是宿主的 配置信息

        // 实例化此方法 final StringBlock[] ensureStringBlocks()
        Method ensureStringBlocksMethod = assetManager.getClass().getDeclaredMethod("ensureStringBlocks");
        ensureStringBlocksMethod.setAccessible(true);
        ensureStringBlocksMethod.invoke(assetManager); // 执行了ensureStringBlocks  string.xml  color.xml   anim.xml 被初始化

        // 特殊：专门加载插件资源
        resources = new Resources(assetManager, r.getDisplayMetrics(), r.getConfiguration());
    }

    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }

    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    /**
     * 自己创造一个LoadedApk.ClassLoader 添加到 mPackages，此LoadedApk 专门用来加载插件里面的 class
     */
    private void customLoadedApkAction() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "p.apk");
        if (!file.exists()) {
            throw new FileNotFoundException("插件包不存在..." + file.getAbsolutePath());
        }
        String pulginPath = file.getAbsolutePath();

        // mPackages 添加 自定义的LoadedApk
        // final ArrayMap<String, WeakReference<LoadedApk>> mPackages 添加自定义LoadedApk
        Class mActivityThreadClass = Class.forName("android.app.ActivityThread");

        // 执行此方法 public static ActivityThread currentActivityThread() 拿到 ActivityThread对象
        Object mActivityThread = mActivityThreadClass.getMethod("currentActivityThread").invoke(null);

        Field mPackagesField = mActivityThreadClass.getDeclaredField("mPackages");
        mPackagesField.setAccessible(true);
        // 拿到mPackages对象
        Object mPackagesObj = mPackagesField.get(mActivityThread);

        Map mPackages = (Map) mPackagesObj;

        // 如何自定义一个 LoadedApk，系统是如何创造LoadedApk的，我们就怎么去创造LoadedApk
        // 执行此 public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai, CompatibilityInfo compatInfo)
        Class mCompatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Field defaultField = mCompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultField.setAccessible(true);
        Object defaultObj = defaultField.get(null);

        /**
         * ApplicationInfo 如何获取，我们之前学习 APK解析源码分析
         */
        ApplicationInfo applicationInfo = getApplicationInfoAction();

        Method mLoadedApkMethod = mActivityThreadClass.getMethod("getPackageInfoNoCheck", ApplicationInfo.class, mCompatibilityInfoClass); // 类类型
        // 执行 才能拿到 LoedApk 对象
        Object mLoadedApk = mLoadedApkMethod.invoke(mActivityThread, applicationInfo, defaultObj);

        // 自定义加载器 加载插件
        // String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent

        File fileDir = getDir("pulginPathDir", Context.MODE_PRIVATE);

        // 自定义 加载插件的 ClassLoader
        ClassLoader classLoader = new PluginClassLoader(pulginPath,fileDir.getAbsolutePath(), null, getClassLoader());

        Field mClassLoaderField = mLoadedApk.getClass().getDeclaredField("mClassLoader");
        mClassLoaderField.setAccessible(true);
        mClassLoaderField.set(mLoadedApk, classLoader); // 替换 LoadedApk 里面的 ClassLoader

        // 添加自定义的 LoadedApk 专门加载 插件里面的 class

        // 最终的目标 mPackages.put(插件的包名，插件的LoadedApk);
        WeakReference weakReference = new WeakReference(mLoadedApk); // 放入 自定义的LoadedApk --》 插件的
        mPackages.put(applicationInfo.packageName, weakReference); // 增加了我们自己的LoadedApk
    }

    /**
     * 获取 ApplicationInfo 为插件服务的
     * @return
     * @throws
     */
    private ApplicationInfo getApplicationInfoAction() throws Exception {
        // 执行此public static ApplicationInfo generateApplicationInfo方法，拿到ApplicationInfo
        Class mPackageParserClass = Class.forName("android.content.pm.PackageParser");

        Object mPackageParser = mPackageParserClass.newInstance();

        // generateApplicationInfo方法的类类型
        Class $PackageClass = Class.forName("android.content.pm.PackageParser$Package");
        Class mPackageUserStateClass = Class.forName("android.content.pm.PackageUserState");

        Method mApplicationInfoMethod = mPackageParserClass.getMethod("generateApplicationInfo",$PackageClass,
                int.class, mPackageUserStateClass);

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "p.apk");
        String pulginPath = file.getAbsolutePath();

        // 执行此public Package parsePackage(File packageFile, int flags)方法，拿到 Package
        // 获得执行方法的对象
        Method mPackageMethod = mPackageParserClass.getMethod("parsePackage", File.class, int.class);
        Object mPackage = mPackageMethod.invoke(mPackageParser, file, PackageManager.GET_ACTIVITIES);

        // 参数 Package p, int flags, PackageUserState state
        ApplicationInfo applicationInfo = (ApplicationInfo)
                mApplicationInfoMethod.invoke(mPackageParser, mPackage, 0, mPackageUserStateClass.newInstance());

        // 获得的 ApplicationInfo 就是插件的 ApplicationInfo
        // 我们这里获取的 ApplicationInfo
        // applicationInfo.publicSourceDir = 插件的路径；
        // applicationInfo.sourceDir = 插件的路径；
        applicationInfo.publicSourceDir = pulginPath;
        applicationInfo.sourceDir = pulginPath;

        return applicationInfo;
    }



    // Hook 拦截此 getPackageInfo 做自己的逻辑
    private void hookGetPackageInfo() {
        try {
            // sPackageManager 替换  我们自己的动态代理
            Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = mActivityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);

            Field sPackageManagerField = mActivityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            final Object packageManager = sPackageManagerField.get(null);

            /**
             * 动态代理
             */
            Class mIPackageManagerClass = Class.forName("android.content.pm.IPackageManager");

            Object mIPackageManagerProxy = Proxy.newProxyInstance(getClassLoader(),

                    new Class[]{mIPackageManagerClass}, // 要监听的接口

                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if ("getPackageInfo".equals(method.getName())) {
                                // 如何才能绕过 PMS, 欺骗系统

                                // pi != null
                                return new PackageInfo(); // 成功绕过 PMS检测
                            }
                            // 让系统正常继续执行下去
                            return method.invoke(packageManager, args);
                        }
                    });


            // 替换  狸猫换太子   换成我们自己的 动态代理
            sPackageManagerField.set(null, mIPackageManagerProxy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
