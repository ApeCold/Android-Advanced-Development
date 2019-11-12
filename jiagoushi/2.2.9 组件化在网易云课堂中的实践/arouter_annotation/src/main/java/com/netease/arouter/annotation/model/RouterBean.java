package com.netease.arouter.annotation.model;

import javax.lang.model.element.Element;

/**
 * 路由路径Path的最终实体封装类
 * 比如：app分组中的MainActivity对象，这个对象有更多的属性
 */
public class RouterBean {

    public enum Type {
        ACTIVITY,
        CALL
    }

    // 枚举类型：Activity
    private Type type;
    // 类节点
    private Element element;
    // 注解使用的类对象
    private Class<?> clazz;
    // 路由地址
    private String path;
    // 路由组
    private String group;

    private RouterBean(Builder builder) {
        this.element = builder.element;
        this.path = builder.path;
        this.group = builder.group;
    }

    private RouterBean(Type type, Class<?> clazz, String path, String group) {
        this.type = type;
        this.clazz = clazz;
        this.path = path;
        this.group = group;
    }

    // 对外提供简易版构造方法，主要是为了方便APT生成代码
    public static RouterBean create(Type type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 构建者模式
     */
    public static class Builder {

        // 类节点
        private Element element;
        // 路由地址
        private String path;
        // 路由组
        private String group;

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        // 最后的build或者create，往往是做参数的校验或者初始化赋值工作
        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path必填项为空，如：/app/MainActivity");
            }
            return new RouterBean(this);
        }
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
