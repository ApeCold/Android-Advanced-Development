package com.netease.hotfix.library.utils;

import java.lang.reflect.Field;

/**
 * <pre>
 *     author  : NetEase
 *     time    : 2019/1/22
 *     version : v1.1.1
 *     qq      : 8950764
 *     email   : 8950764@qq.com
 *     desc    : 反射工具类
 * </pre>
 */
public class ReflectUtils {

    /**
     * 通过反射获取某对象，并设置私有可访问
     *
     * @param obj   该属性所属类的对象
     * @param clazz 该属性所属类
     * @param field 属性名
     * @return 该属性对象
     */
    private static Object getField(Object obj, Class<?> clazz, String field)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field localField = clazz.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    /**
     * 给某属性赋值，并设置私有可访问
     *
     * @param obj   该属性所属类的对象
     * @param clazz 该属性所属类
     * @param value 值
     */
    public static void setField(Object obj, Class<?> clazz, Object value)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field localField = clazz.getDeclaredField("dexElements");
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    /**
     * 通过反射获取BaseDexClassLoader对象中的PathList对象
     *
     * @param baseDexClassLoader BaseDexClassLoader对象
     * @return PathList对象
     */
    public static Object getPathList(Object baseDexClassLoader)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 通过反射获取BaseDexClassLoader对象中的PathList对象，再获取dexElements对象
     *
     * @param paramObject PathList对象
     * @return dexElements对象
     */
    public static Object getDexElements(Object paramObject)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }
}
