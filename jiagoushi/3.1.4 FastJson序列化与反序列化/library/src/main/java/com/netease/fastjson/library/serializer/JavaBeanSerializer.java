package com.netease.fastjson.library.serializer;

import com.netease.fastjson.library.utils.FieldInfo;
import com.netease.fastjson.library.utils.TypeUtils;

import java.util.List;

/**
 * JavaBean序列化器
 */
public class JavaBeanSerializer implements ObjectSerializer {

    private final FieldSerializer[] getters;

    // 构造方法（参考源码JavaBeanSerializer.java 54行）
    JavaBeanSerializer(Class<?> clazz) {
        // 收集 序列化 / 反序列化 对象所有属性信息（参考源码JavaBeanSerializer.java 79行）
        List<FieldInfo> fieldInfoList = TypeUtils.buildBeanInfo(clazz, true);
        // 参考源码JavaBeanSerializer.java 85 - 91行（阉割了序列化后的排序功能TypeUtils.java 1688行）
        getters = new FieldSerializer[fieldInfoList.size()];
        for (int i = 0; i < getters.length; i++) {
            // 属性序列化数组，构造方法传入该属性的信息
            getters[i] = new FieldSerializer(fieldInfoList.get(i));
        }
    }

    @Override
    public void write(SerializeConfig config, Object object, StringBuilder out) {
        // 参考源码JavaBeanSerializer.java 180行
        if (object == null) {
            out.append("null");
            return;
        }

        // 拼接字符串前缀
        out.append("{");
        // 定义局部变量，是否结束。如果结束不需要在尾部加入","
        boolean isEnd = true;
        // 循环属性序列化
        for (FieldSerializer getter : getters) {
            if (!isEnd) {
                out.append(",");
            }
            // 得到每一个属性序列化后的字符串
            String entry = getter.write(config, object);
            // 传入的StringBuilder不断拼接起来
            out.append(entry);
            // 是否最后一个属性序列化
            isEnd = entry.isEmpty();
        }
        // 拼接字符串后缀
        out.append("}");
    }
}
