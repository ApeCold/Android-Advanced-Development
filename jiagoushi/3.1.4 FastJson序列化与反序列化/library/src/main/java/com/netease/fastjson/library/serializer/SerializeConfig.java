package com.netease.fastjson.library.serializer;

import com.netease.fastjson.library.JSONException;
import com.netease.fastjson.library.deserializer.JavaBeanDeserializer;
import com.netease.fastjson.library.deserializer.ObjectDeserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 序列化 / 反序列化配置
 */
public class SerializeConfig {

    // 单例
    public final static SerializeConfig globalInstance = new SerializeConfig();
    // 序列化缓存
    private final Map<Class, ObjectSerializer> serializersCache;
    // 反序列化缓存
    private final Map<Type, ObjectDeserializer> deserializersCache;

    private SerializeConfig() {
        // 缓存集合初始化
        serializersCache = new HashMap<>();
        deserializersCache = new HashMap<>();
    }

    // 参考源码SerializeConfig.java 276行
//    public static SerializeConfig getGlobalInstance() {
//        return globalInstance;
//    }

    /**
     * 对象序列化接口实现
     * 参考源码SerializeConfig.java 433行
     *
     * @param clazz 序列化对象Class
     * @return 接口实现（writer = new JavaBeanSerializer(clazz)）
     */
    ObjectSerializer getObjectWriter(Class<?> clazz) {
        // 从缓存中获取序列化接口
        ObjectSerializer writer = serializersCache.get(clazz);
        if (writer != null) {
            return writer;
        }
        // 匹配序列化对象类型（参考源码SerializeConfig.java 487 - 491往下）
        if (Map.class.isAssignableFrom(clazz)) {
            throw new JSONException("暂未实现Map序列化");
        } else if (List.class.isAssignableFrom(clazz)) {
            throw new JSONException("暂未实现List序列化");
        } else if (clazz.isArray()) {
            throw new JSONException("暂未实现数组序列化");
        }
        // 省略N多匹配（仅做示例参考！！！）
        else {
            // 序列化接口 = 接口实现（参考源码SerializeConfig.java 776 - 106 - 121 - 262行）
            writer = new JavaBeanSerializer(clazz);
        }

        // 加入缓存集合
        serializersCache.put(clazz, writer);
        return writer;
    }

    /**
     * 反序列化对象接口实现
     * 参考源码ParseConfig.java 390行
     *
     * @param type 反序列化对象Class
     * @return 接口实现（derializer = new JavaBeanDeserializer((Class<?>) type)）
     */
    public ObjectDeserializer getDeserializer(Type type) {
        ObjectDeserializer derializer = deserializersCache.get(type);
        if (derializer == null) {
            if (type instanceof Class) {
                // 反序列化接口 = 接口实现（参考源码ParseConfig.java 664行）
                derializer = new JavaBeanDeserializer((Class<?>) type);
            } else if (type instanceof ParameterizedType) { // 参数化类型（List<OrderInfo>）
                throw new JSONException("暂未实现参数化类型反序列化");
            } else if (type instanceof WildcardType) { // 通配符类型（Class<? extends/super UserInfo>）
                throw new JSONException("暂未实现通配符类型反序列化");
            } else {
                throw new JSONException("暂未该类型反序列化");
            }
            // 加入缓存集合
            deserializersCache.put(type, derializer);
        }
        return derializer;
    }
}
