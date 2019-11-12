package com.netease.skin.library.model;

import android.content.res.TypedArray;
import android.util.SparseIntArray;

/**
 * 临时JavaBean对象，用于存储控件的key、value
 * 如：key:android:textColor, value:@Color/xxx
 * <p>
 * 思考：动态加载的场景，键值对是否存储SharedPreferences呢？
 */
public class AttrsBean {

    private SparseIntArray resourcesMap;
    private static final int DEFAULT_VALUE = -1;

    public AttrsBean() {
        resourcesMap = new SparseIntArray();
    }

    /**
     * 储控件的key、value
     *
     * @param typedArray 控件属性的类型集合，如：background / textColor
     * @param styleable  自定义属性，参考value/attrs.xml
     */
    public void saveViewResource(TypedArray typedArray, int[] styleable) {
        for (int i = 0; i < typedArray.length(); i++) {
            int key = styleable[i];
            int resourceId = typedArray.getResourceId(i, DEFAULT_VALUE);
            resourcesMap.put(key, resourceId);
        }
    }

    /**
     * 获取控件某属性的resourceId
     *
     * @param styleable 自定义属性，参考value/attrs.xml
     * @return 某控件某属性的resourceId
     */
    public int getViewResource(int styleable) {
        return resourcesMap.get(styleable);
    }
}
