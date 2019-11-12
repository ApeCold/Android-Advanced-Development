package com.netease.fastjson.library.serializer;

import com.netease.fastjson.library.utils.FieldInfo;

import java.math.BigDecimal;

/**
 * 属性序列化
 */
class FieldSerializer {

    // "name":"simon"中的key是："name":
    private final String key;
    // 序列化对象的属性信息，包含value值
    private final FieldInfo fieldInfo;
    // 是否基本数据类型
    private boolean isPrimitive;

    FieldSerializer(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
        // 拼接："name":
        this.key = '"' + fieldInfo.name + "\":";

        // 获取属性的类型
        Class type = fieldInfo.fieldClass;
        // 是否 基本数据类型 或 包装类
        // isPrimitive()用来判断Class是否为（boolean、char、byte、short、int、long、float、double）
        isPrimitive = isJavaObject(type) || type.isPrimitive();
    }

    /**
     * 序列化字符串拼接
     *
     * @param config 序列化配置
     * @param object 序列化对象
     * @return 序列化后的字符串
     */
    String write(SerializeConfig config, Object object) {
        try {
            // 获取属性（优先方法反射，其次属性get）
            Object o = fieldInfo.get(object);
            if (null == o) {
                return "";
            }
            // 必须new不能传入，否则会出现无限嵌套
            StringBuilder out = new StringBuilder();
            // 如果是基本数据类型
            if (isPrimitive) {
                // "age":35,"isHero":true
                out.append(key);
                out.append(o.toString());
            } else if (isString(fieldInfo.fieldClass)) { // 如果是String类型
                // "name":"simon"
                out.append(key);
                out.append('"');
                out.append(o.toString());
                out.append('"');
            } else {
                // 如果是JavaBean对象，则进入递归。继续序列化该对象的所有属性
                ObjectSerializer objectSerializer = config.getObjectWriter(fieldInfo.fieldClass);
                out.append(key);
                objectSerializer.write(config, o, out);
            }
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 当前类型是否包装类
     *
     * @param type 当前类型
     * @return true为Java包装类
     */
    private boolean isJavaObject(Class type) {
        return type == Integer.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Boolean.class ||
                type == Double.class ||
                type == Float.class ||
                type == BigDecimal.class ||
                type == Short.class;
    }

    /**
     * 是否String类型（有些特殊）
     *
     * @param type 当前类型
     * @return true为String类型
     */
    private boolean isString(Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }
}
