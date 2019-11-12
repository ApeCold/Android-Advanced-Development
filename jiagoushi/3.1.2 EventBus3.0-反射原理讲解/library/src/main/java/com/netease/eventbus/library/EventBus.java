package com.netease.eventbus.library;

import android.os.Handler;
import android.os.Looper;

import com.netease.eventbus.library.annotation.Subscribe;
import com.netease.eventbus.library.core.MethodManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBus {

    // volatile修饰的变量不允许线程内部缓存和重排序,即直接修改内存
    private static volatile EventBus instance;
    // 用来保存这些带注解的方法（订阅者的回调方法）
    private Map<Object, List<MethodManager>> cacheMap;

    private Handler handler;
    private ExecutorService executorService;

    private EventBus() {
        cacheMap = new HashMap<>();

        // Handler高级用法：将handler放在主线程使用
        handler = new Handler(Looper.getMainLooper());
        // 创建一个子线程（缓存线程池）
        executorService = Executors.newCachedThreadPool();
    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    // 找到MainActivity所有带符合注解的方法
    public void register(Object getter) {
        // 获取MainActivity所有的方法
        List<MethodManager> methodList = cacheMap.get(getter);
        if (methodList == null) { // 不为空表示以前注册完成
            methodList = findAnnotationMethod(getter);
            cacheMap.put(getter, methodList);
        }
    }

    // 获取MainActivity中所有注解的方法
    private List<MethodManager> findAnnotationMethod(Object getter) {
        List<MethodManager> methodList = new ArrayList<>();
        // 获取类
        Class<?> clazz = getter.getClass();
        // 获取所有方法
        Method[] methods = clazz.getMethods();

        // 性能优化。N个父类不可能有自定义注解。排除后再反射
        while (clazz != null) {
            // 找出系统类，直接跳出，不添加cacheMap（因为不是订阅者）
            String clazzName = clazz.getName();
            if (clazzName.startsWith("java.") || clazzName.startsWith("javax.")
                    || clazzName.startsWith("android.")) {
                break;
            }

            // 循环方法
            for (Method method : methods) {
                // 获取方法的注解
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                // 判断注解不为空，切记不能抛异常
                if (subscribe == null) {
                    continue;
                }
                // 严格控制方法格式和规范
                // 方法必须是返回void（一次匹配）
                Type returnType = method.getGenericReturnType();
                if (!"void".equals(returnType.toString())) {
                    throw new RuntimeException(method.getName() + "方法返回必须是void");
                }
                // 方法参数必须有值（二次匹配）
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException(method.getName() + "方法有且只有一个参数");
                }

                // 完全符合要求、规范的方法，保存到方法对象中MethodManager（3个重要成员：方法、参数、线程）
                MethodManager manager = new MethodManager(parameterTypes[0], subscribe.threadMode(), method);
                methodList.add(manager);
            }

            // 不断循环找出父类含有订阅者（注解方法）的类。直到为空，比如AppCompatActivity没有吧
            clazz = clazz.getSuperclass();
        }
        return methodList;
    }

    // SecondActivity发送消息
    public void post(final Object setter) {
        // 订阅者已经登记，从登记表中找出
        Set<Object> set = cacheMap.keySet();
        // 比如获取MainActivity对象
        for (final Object getter : set) {
            // 获取MainActivity中所有注解的方法
            List<MethodManager> methodList = cacheMap.get(getter);
            if (methodList != null) {
                // 循环每个方法
                for (final MethodManager method : methodList) {
                    // 有可能多个方法的参数一样，从而都同时收到发送的消息
                    // 通过EventBean来判断是否匹配上
                    if (method.getType().isAssignableFrom(setter.getClass())) {
                        // 通过方法的类型匹配，从SecondActivity发送的EventBean对象（参数）
                        // 匹配MainActivity中所有注解的方法符合要求的，都发送消息

                        // class1.isAssignableFrom(class2) 判定此 Class 对象所表示的类或接口
                        // 与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口

                        // 线程调度
                        switch (method.getThreadMode()) {
                            case POSTING:
                                invoke(method, getter, setter);
                                break;

                            case MAIN:
                                // 先判断发送方是否在主线程
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    invoke(method, getter, setter);
                                } else { // 子线程 - 主线程，切换线程（用到Handler）
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            invoke(method, getter, setter);
                                        }
                                    });
                                }
                                break;

                            case BACKGROUND:
                                // 先判断发送方是否在主线程
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    // 主线程 - 子线程，创建一个子线程（缓存线程池）
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            invoke(method, getter, setter);
                                        }
                                    });
                                } else { // 子线程 到 子线程，不用切换线程
                                    invoke(method, getter, setter);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    // 找到匹配方法后，通过反射调用MainActivity中所有符合要求的方法
    private void invoke(MethodManager method, Object getter, Object setter) {
        Method execute = method.getMethod();
        try {
            execute.invoke(getter, setter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
