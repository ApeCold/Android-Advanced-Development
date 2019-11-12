package com.netease.fastjson.library.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 序列化对象的属性信息实体类
 */
public class FieldInfo {

    public final String name; // 属性名
    private final Method method; // 属性 getter/setter 方法
    private final Field field; // 属性
    public final Class<?> fieldClass; // 属性类型
    public Type genericType; // 属性类型

    FieldInfo(String name, Method method, Field field) {
        this(name, method, field, false);
    }

    FieldInfo(String name, Method method, Field field, boolean isSetters) {
        this.name = name;
        this.field = field;
        this.method = method;
        // 参考源码FieldInfo.java 178 - 186 - 192行
        fieldClass = method != null ? method.getReturnType() : field.getType();

        // 反序列化时进入，过滤了方法只有一个参数
        if (isSetters) {
            // 获取属性的类型
            if (null != method) {
                genericType = method.getGenericParameterTypes()[0];
            } else {
                genericType = field.getGenericType();
            }
        }
    }

    // 参考源码FieldInfo.java 489行
    public Object get(Object javaObject) throws IllegalAccessException, InvocationTargetException {
        return method != null
                ? method.invoke(javaObject)
                : field.get(javaObject);
    }

    // 参考源码FieldInfo.java 495行
    public void set(Object javaObject, Object value) throws IllegalAccessException, InvocationTargetException {
        if (method != null) {
            // 处理BigDecimal
//            Class<?>[] parameterTypes = method.getParameterTypes();
//            if (parameterTypes[0] == BigDecimal.class) {
//                method.invoke(javaObject, new BigDecimal(value.toString()));
//                return;
//            }
            method.invoke(javaObject, value);
            return;
        }

        // 如果属性为私有，设置允许访问
        if (field.isAccessible()) field.setAccessible(true);
        field.set(javaObject, value);
    }
}
