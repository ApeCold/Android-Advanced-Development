package com.netease.permissions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// SOURCE 注解仅在源码中保留,class文件中不存在
// CLASS 注解在源码和class文件中都存在,但运行时不存在,即运行时无法获得,该策略也是默认的保留策略
// RUNTIME 注解在源码,class文件中存在且运行时可以通过反射机制获取到
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface NeedsPermission {
}