package com.netease.andfix;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

public class DexManager {
    private static final String     TAG         = "DexManager";
    private static final DexManager ourInstance = new DexManager();

    public static DexManager getInstance() {
        return ourInstance;
    }

    private DexManager() {
    }

    private Context mContext;

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void loadDex(File file) {
        try {
            DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(),
                    new File(mContext.getCacheDir(), "opt").getAbsolutePath(),
                    Context.MODE_PRIVATE);
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String className = entries.nextElement();
                //                Class.forName(className);//为什么不能用这种方式？这种方式只能获取安装app中的class
                Class fixClazz = dexFile.loadClass(className, mContext.getClassLoader());
                if (fixClazz != null) {
                    fixClass(fixClazz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fixClass(Class fixClazz) {
        Method[] methods = fixClazz.getDeclaredMethods();
        //        Method[] methods = fixClazz.getMethods();
        MethodReplace methodReplace;
        String className;
        String methodName;
        Class<?> bugClass;
        Method bugMethod;
        for (Method fixMethod : methods) {
            methodReplace = fixMethod.getAnnotation(MethodReplace.class);
            if (methodReplace == null) {
                continue;
            }
            Log.e(TAG, "找到修复好的方法: " + fixMethod.getDeclaringClass() + "@" + fixMethod.getName());
            className = methodReplace.className();
            methodName = methodReplace.methodName();
            if (!TextUtils.isEmpty(className) && !TextUtils.isEmpty(methodName)) {
                try {
                    bugClass = Class.forName(className);
                    bugMethod =
                            bugClass.getDeclaredMethod(methodName, fixMethod.getParameterTypes());
                    replace(bugMethod, fixMethod);
                    Log.e(TAG, "修复完成！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Log.e(TAG, "/修复好的方法未设置自定义注解属性");
            }
        }
    }

    private native void replace(Method bugMethod, Method fixMethod);

    static {
        System.loadLibrary("native-lib");
    }
}
