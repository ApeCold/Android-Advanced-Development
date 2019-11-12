package com.netease.fastjson.library;

import com.netease.fastjson.library.deserializer.ObjectDeserializer;
import com.netease.fastjson.library.serializer.JSONSerializer;
import com.netease.fastjson.library.serializer.SerializeConfig;

import java.lang.reflect.Type;

/**
 * 源码参考版本：com.alibaba:fastjson:1.2.58
 */
public class JSON {

    /**
     * 序列化对象，将JavaBean转为字符串（参考源码JSON.java 678 - 718行）
     *
     * @param object 需要序列化的对象
     * @return 序列化后的字符串
     */
    public static String toJSONString(Object object) {
        // 参考源码JSON.java 724行
        // SerializeWriter out = new SerializeWriter(null, defaultFeatures, features);
        // 简化为字符串拼接：
        StringBuilder out = new StringBuilder();
        // 创建JSON序列化器
        JSONSerializer serializer = new JSONSerializer(out, SerializeConfig.globalInstance);
        // 调用序列化方法
        serializer.write(object);
        return out.toString();
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return parseObject(json, clazz, SerializeConfig.globalInstance);
    }

    /**
     * 反序列化对象，将json字符串转为JavaBean对象
     *
     * @param json   字符串
     * @param type   JavaBean对象Class
     * @param config 序列化 / 反序列化配置
     * @param <T>    JavaBean对象
     * @return JavaBean对象
     */
    private static <T> T parseObject(String json, Type type, SerializeConfig config) {
        // 参考源码JSON.java 350行
        if (json == null) {
            return null;
        }
        // 反序列化接口实现（参考源码JSON.java 378 - DefaultJSONParser.java 683行）
        ObjectDeserializer deserializer = config.getDeserializer(type);
        // 调用反序列化方法（将json字符串转为javabean对象赋值）
        return deserializer.deserialze(config, json, null);
    }
}
