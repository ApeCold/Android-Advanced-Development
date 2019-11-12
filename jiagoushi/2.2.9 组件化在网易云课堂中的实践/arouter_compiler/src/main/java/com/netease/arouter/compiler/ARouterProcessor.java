package com.netease.arouter.compiler;

import com.google.auto.service.AutoService;
import com.netease.arouter.annotation.ARouter;
import com.netease.arouter.annotation.model.RouterBean;
import com.netease.arouter.compiler.utils.Constants;
import com.netease.arouter.compiler.utils.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 编码此类1句话：细心再细心，出了问题debug真的不好调试
 */
// AutoService则是固定的写法，加个注解即可
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 注解处理器接收的参数
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE})
public class ARouterProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的类文件，class文件以及辅助文件
    private Filer filer;

    // 子模块名，如：app/order/personal。需要拼接类名时用到（必传）ARouter$$Group$$order
    private String moduleName;

    // 包名，用于存放APT生成的类文件
    private String packageNameForAPT;

    // 临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
    // key:组名"app", value:"app"组的路由路径"ARouter$$Path$$app.class"
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    // 临时map存储，用来存放路由Group信息，生成路由组类文件时遍历
    // key:组名"app", value:类名"ARouter$$Path$$app.class"
    private Map<String, String> tempGroupMap = new HashMap<>();

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        // 通过ProcessingEnvironment去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);
            // 有坑：Diagnostic.Kind.ERROR，异常会自动结束，不像安卓中Log.e
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>> " + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName >>> " + packageNameForAPT);
        }

        // 必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或者packageName为空，请在对应build.gradle配置参数");
        }
    }

    /**
     * 相当于main函数，开始处理注解
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合
     * @param roundEnvironment 当前或是之前的运行环境,可以通过该对象查找的注解。
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 一旦有类之上使用@ARouter注解
        if (!EmptyUtils.isEmpty(set)) {
            // 获取所有被 @ARouter 注解的 元素集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);

            if (!EmptyUtils.isEmpty(elements)) {
                // 解析元素
                try {
                    parseElements(elements);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 坑：必须写返回值，表示处理@ARouter注解完成
            return true;
        }
        return false;
    }

    // 解析所有被 @ARouter 注解的 类元素集合
    private void parseElements(Set<? extends Element> elements) throws IOException {
        // 通过Element工具类，获取Activity、Callback类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        TypeElement callType = elementUtils.getTypeElement(Constants.CALL);

        // 显示类信息（获取被注解节点，类节点）这里也叫自描述 Mirror
        TypeMirror activityMirror = activityType.asType();
        TypeMirror callMirror = callType.asType();

        // 遍历节点
        for (Element element : elements) {
            // 获取每个元素类信息，用于比较
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历元素信息：" + elementMirror.toString());

            // 获取每个类上的@ARouter注解中的注解值
            ARouter aRouter = element.getAnnotation(ARouter.class);

            // 路由详细信息，最终实体封装类
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();

            // 高级判断：ARouter注解仅能用在类之上，并且是规定的Activity
            // 类型工具类方法isSubtype，相当于instance一样
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouterBean.Type.ACTIVITY);
            } else if (typeUtils.isSubtype(elementMirror, callMirror)) {
                bean.setType(RouterBean.Type.CALL);
            } else {
                // 不匹配抛出异常，这里谨慎使用！考虑维护问题
                throw new RuntimeException("@ARouter注解目前仅限用于Activity类之上");
            }

            // 赋值临时map存储，用来存放路由组Group对应的详细Path类对象
            valueOfPathMap(bean);
        }

        // routerMap遍历后，用来生成类文件

        // 获取ARouterLoadGroup、ARouterLoadPath类型（生成类文件需要实现的接口）
        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.AROUTE_GROUP); // 组接口
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.AROUTE_PATH); // 路径接口

        // 第一步：生成路由组Group对应详细Path类文件，如：ARouter$$Path$$app
        createPathFile(pathLoadType);

        // 第二步：生成路由组Group类文件（没有第一步，取不到类文件），如：ARouter$$Group$$app
        createGroupFile(groupLoadType, pathLoadType);
    }

    /**
     * 生成路由组Group对应详细Path，如：ARouter$$Path$$app
     *
     * @param pathLoadType ARouterLoadPath接口信息
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        // 判断是否有需要生成的类文件
        if (EmptyUtils.isEmpty(tempPathMap)) return;

        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // Map<String,
                ClassName.get(RouterBean.class) // Map<String, RouterBean>
        );

        // 遍历分组，每一个分组创建一个路径类文件，如：ARouter$$Path$$app
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            // 方法配置：public Map<String, RouterBean> loadPath() {
            MethodSpec.Builder methodBuidler = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME) // 方法名
                    .addAnnotation(Override.class) // 重写注解
                    .addModifiers(Modifier.PUBLIC) // public修饰符
                    .returns(methodReturns); // 方法返回值

            // 遍历之前：Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuidler.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    HashMap.class);

            // 一个分组，如：ARouter$$Path$$app。有很多详细路径信息，如：/app/MainActivity、/app/OtherActivity
            List<RouterBean> pathList = entry.getValue();
            // 方法内容配置（遍历每个分组中每个路由详细路径）
            for (RouterBean bean : pathList) {
                // 类似String.format("hello %s net163 %d", "net", 163)通配符
                // pathMap.put("/app/MainActivity", RouterBean.create(
                //        RouterBean.Type.ACTIVITY, MainActivity.class, "/app/MainActivity", "app"));
                methodBuidler.addStatement(
                        "$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        Constants.PATH_PARAMETER_NAME, // pathMap.put
                        bean.getPath(), // "/app/MainActivity"
                        ClassName.get(RouterBean.class), // RouterBean
                        ClassName.get(RouterBean.Type.class), // RouterBean.Type
                        bean.getType(), // 枚举类型：ACTIVITY
                        ClassName.get((TypeElement) bean.getElement()), // MainActivity.class
                        bean.getPath(), // 路径名
                        bean.getGroup() // 组名
                );
            }

            // 遍历之后：return pathMap;
            methodBuidler.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            // 最终生成的类文件名
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件：" +
                    packageNameForAPT + "." + finalClassName);

            // 生成类文件：ARouter$$Path$$app
            JavaFile.builder(packageNameForAPT, // 包名
                    TypeSpec.classBuilder(finalClassName) // 类名
                            .addSuperinterface(ClassName.get(pathLoadType)) // 实现ARouterLoadPath接口
                            .addModifiers(Modifier.PUBLIC) // public修饰符
                            .addMethod(methodBuidler.build()) // 方法的构建（方法参数 + 方法体）
                            .build()) // 类构建完成
                    .build() // JavaFile构建完成
                    .writeTo(filer); // 文件生成器开始生成类文件

            // 非常重要一步！！！！！路径文件生成出来了，才能赋值路由组tempGroupMap
            tempGroupMap.put(entry.getKey(), finalClassName);
        }
    }

    /**
     * 生成路由组Group文件，如：ARouter$$Group$$app
     *
     * @param groupLoadType ARouterLoadGroup接口信息
     * @param pathLoadType ARouterLoadPath接口信息
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        // 判断是否有需要生成的类文件
        if (EmptyUtils.isEmpty(tempGroupMap) || EmptyUtils.isEmpty(tempPathMap)) return;

        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // Map<String,
                // 第二个参数：Class<? extends ARouterLoadPath>
                // 某某Class是否属于ARouterLoadPath接口的实现类
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        // 方法配置：public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        MethodSpec.Builder methodBuidler = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME) // 方法名
                .addAnnotation(Override.class) // 重写注解
                .addModifiers(Modifier.PUBLIC) // public修饰符
                .returns(methodReturns); // 方法返回值

        // 遍历之前：Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        methodBuidler.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class);

        // 方法内容配置
        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            // 类似String.format("hello %s net163 %d", "net", 163)通配符
            // groupMap.put("main", ARouter$$Path$$app.class);
            methodBuidler.addStatement("$N.put($S, $T.class)",
                    Constants.GROUP_PARAMETER_NAME, // groupMap.put
                    entry.getKey(),
                    // 类文件在指定包名下
                    ClassName.get(packageNameForAPT, entry.getValue()));
        }

        // 遍历之后：return groupMap;
        methodBuidler.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        // 最终生成的类文件名
        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                packageNameForAPT + "." + finalClassName);

        // 生成类文件：ARouter$$Group$$app
        JavaFile.builder(packageNameForAPT, // 包名
                TypeSpec.classBuilder(finalClassName) // 类名
                        .addSuperinterface(ClassName.get(groupLoadType)) // 实现ARouterLoadGroup接口
                        .addModifiers(Modifier.PUBLIC) // public修饰符
                        .addMethod(methodBuidler.build()) // 方法的构建（方法参数 + 方法体）
                        .build()) // 类构建完成
                .build() // JavaFile构建完成
                .writeTo(filer); // 文件生成器开始生成类文件
    }

    /**
     * 赋值临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private void valueOfPathMap(RouterBean bean) {
        if (checkRouterPath(bean)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>> " + bean.toString());

            // 开始赋值Map
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            // 如果从Map中找不到key为：bean.getGroup()的数据，就新建List集合再添加进Map
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                routerBeans.add(bean);
                tempPathMap.put(bean.getGroup(), routerBeans);
            } else { // 找到了key，直接加入List集合
                routerBeans.add(bean);
            }

        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
        }
    }

    /**
     * 校验@ARouter注解的值，如果group未填写就从必填项path中截取数据
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        // @ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的path值，必须要以 / 开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        // @ARouter注解中的group有赋值情况
        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和子模块名一致！");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }

        return true;
    }
}
