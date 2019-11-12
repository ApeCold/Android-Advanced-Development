package com.netease.fastjson.library.deserializer;

import com.netease.fastjson.library.serializer.SerializeConfig;

/**
 * 反序列化接口
 */
public interface ObjectDeserializer {

    /**
     * 反序列化对象
     *
     * @param config    序列化 / 反序列化配置
     * @param json      json字符串
     * @param fieldName 对象属性（javabean对象中包含另一个javabean属性）
     * @param <T>       泛型
     * @return 指定的反序列化对象
     */
    <T> T deserialze(SerializeConfig config, String json, Object fieldName);
}
