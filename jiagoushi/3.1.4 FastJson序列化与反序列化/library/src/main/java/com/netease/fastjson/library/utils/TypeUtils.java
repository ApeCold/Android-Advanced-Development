package com.netease.fastjson.library.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeUtils {

    /**
     * 收集 序列化 / 反序列化 对象所有属性信息（参考源码TypeUtils.java 1614 - 1620行）
     *
     * @param beanType     序列化对象Class
     * @param isSerializer true为序列化，false为反序列化
     * @return 序列化 / 反序列化 对象所有属性信息
     */
    public static List<FieldInfo> buildBeanInfo(Class<?> beanType, boolean isSerializer) {
        // 新建属性缓存集合
        Map<String, Field> fieldCacheMap = new HashMap<>();
        // 解析所有属性到缓存集合中（参考源码TypeUtils.java 1674行（合并到一个工具类））
        parserAllFieldToCache(beanType, fieldCacheMap);
        // false进入源码TypeUtils.java 1677行
        return isSerializer ? computeGetters(beanType, fieldCacheMap) : computeSetters(beanType, fieldCacheMap);
    }

    /**
     * 计算 / 收集getter方法（参考源码TypeUtils.java 1722行）
     *
     * @param clazz         序列化对象Class
     * @param fieldCacheMap 属性缓存集合
     * @return 序列化 / 反序列化 对象所有属性信息
     */
    private static List<FieldInfo> computeGetters(Class<?> clazz, Map<String, Field> fieldCacheMap) {
        // 集合key：属性名，value：属性信息
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<>();
        // 获取序列化对象当前类和父类的所有public方法
        Method[] methods = clazz.getMethods();
        // 局部变量：属性名
        String propertyName = "";
        // 遍历序列化对象所有方法
        for (Method method : methods) {
            // 获取方法名
            String methodName = method.getName();
            // get方法不能为static静态方法
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            // get方法返回值不能为void
            if (method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            // get方法不能有参数
            if (method.getParameterTypes().length != 0) {
                continue;
            }
            // 省略……（参考源码TypeUtils.java 1741往下）

            // 参考源码TypeUtils.java 1850行
            if (methodName.startsWith("get")) { // 有些属性没有定义全局，只有get或者is方法
                // 方法名至少为：getX()，不能为get()
                if (methodName.length() < 4) {
                    continue;
                }
                // 方法名不能为getClass()
                if (methodName.equals("getClass")) {
                    continue;
                }

                // 将getXyz()转为：xyz属性名（索引012为get，索引3大写转小写，截取到最后）
                propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }

            // boolean方法的get是is开头（private boolean isHero;）（参考源码TypeUtils.java 1931行）
            if (methodName.startsWith("is")) { // 有些属性没有定义全局，只有get或者is方法
                // 方法名至少为：isX()，不能为is()
                if (methodName.length() < 3) {
                    continue;
                }
                // 方法返回值必须是基本数据类型boolean或者包装类Boolean
                if (method.getReturnType() != Boolean.TYPE
                        && method.getReturnType() != Boolean.class) {
                    continue;
                }

                // 将isXyz()转为：xyz属性名（索引01为is，索引2大写转小写，截取到最后）
                String temp = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
                // 优先选择get，源码TypeUtils.java 1996行（修改版）
                if (fieldInfoMap.containsKey(temp)) {
                    // 集合中有，说明get方法以及存储了
                    continue;
                }

                // 如果集合中没有，则继续往下
                propertyName = temp;
            }

            // 剔除符合条件hashCode方法
            if (methodName.startsWith("hashCode")) {
                continue;
            }

            // 剔除符合条件toString方法
            if (methodName.startsWith("toString")) {
                continue;
            }

            // 很奇怪的地方：源码TypeUtils.java 1927 / 2000两处代码完全一样。个人优化如上92行
            // 从clazz.getDeclaredFields()缓存中获取属性（参考源码：ParseConfig.java 920行）
            Field field = fieldCacheMap.get(propertyName);
            if (field != null) { // 省略了一些条件筛选，做健壮判断
                // 新增属性 getter / setter 方法（参考源码TypeUtils.java 1927 / 2000行）
                FieldInfo fieldInfo = new FieldInfo(propertyName, method, field);
                // 将序列化对象的每个属性信息加入集合（get / is）
                fieldInfoMap.put(propertyName, fieldInfo);
            }
        }

        // 同样有些属性是public定义的，但是没提供getter / setter方法（参考源码TypeUtils.java 2005行）
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            // 属性不允许static静态修饰
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // 属性名
            String temp = field.getName();
            // 优先选择 get / is 获取的属性（集合中包含判断）
            if (fieldInfoMap.containsKey(temp)) {
                continue;
            }
            propertyName = temp;

            // 此处需要再重复一次代码是因为方法循环之外（可抽取）（参考源码TypeUtils.java 2082行）
            FieldInfo fieldInfo = new FieldInfo(propertyName, null, field);
            // 加入集合，最后返回集合的value。也就是序列化对象的所有属性信息
            fieldInfoMap.put(propertyName, fieldInfo);
        }

        // 简化代码：源码TypeUtils.java 2007 - 2010 - 2034 - 2041行
        return new ArrayList<>(fieldInfoMap.values());
    }

    /**
     * 计算 / 收集getter方法
     * 参考源码TypeUtils.java 1722行
     *
     * @param clazz 反序列化对象Class
     * @param fieldCacheMap 属性缓存集合
     * @return 序列化 / 反序列化 对象所有属性信息
     */
    private static List<FieldInfo> computeSetters(Class<?> clazz, Map<String, Field> fieldCacheMap) {
        // 集合key：属性名，value：属性信息
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<>();
        // 获取序列化对象当前类和父类的所有public方法
        Method[] methods = clazz.getMethods();
        // 局部变量：属性名
        String propertyName = "";
        // 遍历序列化对象所有方法
        for (Method method : methods) {
            // 获取方法名
            String methodName = method.getName();
            // set方法不能为static静态方法
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            // set方法返回值必须为void
            if (!method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            // set方法有且只有一个参数
            if (method.getParameterTypes().length != 1) {
                continue;
            }
            // 剔除符合条件wait方法
            if (methodName.startsWith("wait")) {
                continue;
            }

            if (methodName.startsWith("set")) {
                // 方法名至少为：setX()
                if (methodName.length() < 4) {
                    continue;
                }

                // 将setXyz()转为：xyz属性名
                propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }

            // 从clazz.getDeclaredFields()缓存中获取属性
            Field field = fieldCacheMap.get(propertyName);
            if (field != null) { // 省略了一些条件筛选，做健壮判断
                // 新增属性 getter / setter 方法
                FieldInfo fieldInfo = new FieldInfo(propertyName, method, field, true);
                // 将反序列化对象的每个属性信息加入集合（set）
                fieldInfoMap.put(propertyName, fieldInfo);
            }
        }

        // 同样有些属性是public定义的，但是没提供getter / setter方法
        for (Field field : clazz.getFields()) {
            // 属性不允许static静态修饰或final修饰
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            String temp = field.getName();
            // 优先选择set
            if (fieldInfoMap.containsKey(temp)) {
                continue;
            }
            propertyName = temp;

            // 此处需要再重复一次代码是因为方法循环之外（可抽取）
            FieldInfo fieldInfo = new FieldInfo(propertyName, null, field, true);
            // 加入集合，最后返回集合的value。也就是反序列化对象的所有属性信息
            fieldInfoMap.put(propertyName, fieldInfo);
        }

        // 简化代码：源码TypeUtils.java 2007 - 2010 - 2034 - 2041行
        return new ArrayList<>(fieldInfoMap.values());
    }

    /**
     * fieldName,field ，先生成fieldName的快照，减少之后的findField的轮询（官方注释）
     * 解析所有属性到缓存集合中
     *
     * @param clazz         序列化对象Class
     * @param fieldCacheMap 属性缓存集合
     */
    private static void parserAllFieldToCache(Class<?> clazz, Map<String, Field> fieldCacheMap) {
        // 获取当前序列化对象所有属性（getFields()获取当前类和父类所有public属性）
        Field[] fields = clazz.getDeclaredFields();
        // 遍历属性
        for (Field field : fields) {
            // 获取属性名
            String fieldName = field.getName();
            // 如果缓存集合中没有该属性，则加入
            if (!fieldCacheMap.containsKey(fieldName)) {
                fieldCacheMap.put(fieldName, field);
            }
        }
        // 递归寻找当前序列化对象的父类。直至Object为止（如UserInfo extends BaseUser）
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parserAllFieldToCache(clazz.getSuperclass(), fieldCacheMap);
        }
    }
}
