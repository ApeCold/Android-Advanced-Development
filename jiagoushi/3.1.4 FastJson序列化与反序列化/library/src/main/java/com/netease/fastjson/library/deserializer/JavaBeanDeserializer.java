package com.netease.fastjson.library.deserializer;

import com.netease.fastjson.library.serializer.SerializeConfig;
import com.netease.fastjson.library.utils.FieldInfo;
import com.netease.fastjson.library.utils.TypeUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * JavaBean反序列化器
 */
public class JavaBeanDeserializer implements ObjectDeserializer {

    private final Class<?> clazz; // 反序列化后的class对象
    private final List<FieldInfo> fieldInfos; // 反序列化对象时，所有属性

    public JavaBeanDeserializer(Class<?> clazz) {
        this.clazz = clazz;
        // 收集 序列化 / 反序列化 对象所有属性信息
        fieldInfos = TypeUtils.buildBeanInfo(clazz, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(SerializeConfig config, String json, Object fieldName) {
        try {
            // 可以导入jar包，目前android环境
            JSONObject jsonObject;
            if (null == fieldName) {
                // 如果属性是字符串
                jsonObject = new JSONObject(json);
            } else {
                // 如果属性是对象
                jsonObject = (JSONObject) fieldName;
            }
            // 实例化反序列对象（注意空构造方法）
            T t = (T) clazz.newInstance();
            // 循环属性赋值
            for (FieldInfo fieldInfo : fieldInfos) {
                if (!jsonObject.has(fieldInfo.name)) {
                    continue;
                }
                // 获取属性值
                Object value = jsonObject.get(fieldInfo.name);
                if (value instanceof JSONObject) {
                    // 获取反序列化器
                    ObjectDeserializer deserializer = config.getDeserializer(fieldInfo.genericType);
                    // 通过反序列化获取赋值的属性
                    Object obj = deserializer.deserialze(config, null, value);
                    // 优先set方法赋值，其次属性反射赋值
                    fieldInfo.set(t, obj);
                } else {
                    // 为空NULL情况
                    if (value != JSONObject.NULL) {
                        fieldInfo.set(t, value);
                    }
                }
            }

            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
