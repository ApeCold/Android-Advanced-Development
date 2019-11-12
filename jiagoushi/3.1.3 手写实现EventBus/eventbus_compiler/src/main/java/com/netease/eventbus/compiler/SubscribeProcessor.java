package com.netease.eventbus.compiler;

import com.google.auto.service.AutoService;
import com.netease.eventbus.annotation.Subscribe;
import com.netease.eventbus.annotation.mode.EventBeans;
import com.netease.eventbus.annotation.mode.SubscriberInfo;
import com.netease.eventbus.annotation.mode.SubscriberMethod;
import com.netease.eventbus.annotation.mode.ThreadMode;
import com.netease.eventbus.compiler.utils.Constants;
import com.netease.eventbus.compiler.utils.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 编码此类1句话：细心再细心，出了问题debug真的不好调试
 */
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.SUBSCRIBE_ANNOTATION_TYPES})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 注解处理器接收的参数
@SupportedOptions({Constants.PACKAGE_NAME, Constants.CLASS_NAME})
public class SubscribeProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的类文件，class文件以及辅助文件
    private Filer filer;

    // APT包名
    private String packageName;

    // APT类名
    private String className;

    // 临时map存储，用来存放订阅方法信息，生成路由组类文件时遍历
    // key:组名"MainActivity", value:MainActivity中订阅方法集合
    private final Map<TypeElement, List<ExecutableElement>> methodsByClass = new HashMap<>();

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 初始化
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        // 通过ProcessingEnvironment去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            packageName = options.get(Constants.PACKAGE_NAME);
            className = options.get(Constants.CLASS_NAME);
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "packageName >>> " + packageName + " / className >>> " + className);
        }

        // 必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtils.isEmpty(packageName) || EmptyUtils.isEmpty(className)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "注解处理器需要的参数为空，请在对应build.gradle配置参数");
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
        // 一旦有类之上使用@Subscribe注解
        if (!EmptyUtils.isEmpty(set)) {
            // 获取所有被 @Subscribe 注解的 元素集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Subscribe.class);

            if (!EmptyUtils.isEmpty(elements)) {
                // 解析元素
                try {
                    parseElements(elements);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    // 解析所有被 @Subscribe 注解的 类元素集合
    private void parseElements(Set<? extends Element> elements) throws IOException {

        // 遍历节点
        for (Element element : elements) {
            // @Subscribe注解只能在方法之上（尽量避免使用instanceof进行判断）
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "仅解析@Subscribe注解在方法上元素");
                return;
            }
            // 强转方法元素
            ExecutableElement method = (ExecutableElement) element;
            // 检查方法，条件：订阅方法必须是非静态的，公开的，参数只能有一个
            if (checkHasNoErrors(method)) {
                // 获取封装订阅方法的类（方法上一个节点）
                TypeElement classElement = (TypeElement) method.getEnclosingElement();

                // 以类名为key，保存订阅方法
                List<ExecutableElement> methods = methodsByClass.get(classElement);
                if (methods == null) {
                    methods = new ArrayList<>();
                    methodsByClass.put(classElement, methods);
                }
                methods.add(method);
            }

            messager.printMessage(Diagnostic.Kind.NOTE, "遍历注解方法：" + method.getSimpleName().toString());
        }

        // 通过Element工具类，获取SubscriberInfoIndex类型
        TypeElement subscriberIndexType = elementUtils.getTypeElement(Constants.SUBSCRIBERINFO_INDEX);

        // 生成类文件
        createFile(subscriberIndexType);
    }

    private void createFile(TypeElement subscriberIndexType) throws IOException {
        // 添加静态块代码：SUBSCRIBER_INDEX = new HashMap<Class, SubscriberInfo>();
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$N = new $T<$T, $T>()",
                Constants.FIELD_NAME,
                HashMap.class,
                Class.class,
                SubscriberInfo.class);

        // 双层循环，第一层遍历被@Subscribe注解的方法所属类。第二层遍历每个类中所有订阅的方法
        for (Map.Entry<TypeElement, List<ExecutableElement>> entry : methodsByClass.entrySet()) {
            // 此处不能使用codeBlock，会造成错误嵌套
            CodeBlock.Builder contentBlock = CodeBlock.builder();
            CodeBlock contentCode = null;
            String format;
            for (int i = 0; i < entry.getValue().size(); i++) {
                // 获取每个方法上的@Subscribe注解中的注解值
                Subscribe subscribe = entry.getValue().get(i).getAnnotation(Subscribe.class);
                // 获取订阅事件方法所有参数
                List<? extends VariableElement> parameters = entry.getValue().get(i).getParameters();
                // 获取订阅事件方法名
                String methodName = entry.getValue().get(i).getSimpleName().toString();
                // 注意：此处还可以做检查工作，比如：参数类型必须是类或接口类型（这里缩减了）
                TypeElement parameterElement = (TypeElement) typeUtils.asElement(parameters.get(0).asType());
                // 如果是最后一个添加，则无需逗号结尾
                if (i == entry.getValue().size() - 1) {
                    format = "new $T($T.class, $S, $T.class, $T.$L, $L, $L)";
                } else {
                    format = "new $T($T.class, $S, $T.class, $T.$L, $L, $L),\n";
                }
                // new SubscriberMethod(MainActivity.class, "abc", UserInfo.class, ThreadMode.POSTING, 0, false)
                contentCode = contentBlock.add(format,
                        SubscriberMethod.class,
                        ClassName.get(entry.getKey()),
                        methodName,
                        ClassName.get(parameterElement),
                        ThreadMode.class,
                        subscribe.threadMode(),
                        subscribe.priority(),
                        subscribe.sticky())
                        .build();
            }

            if (contentCode != null) {
                // putIndex(new EventBeans(MainActivity.class, new SubscriberMethod[] {)
                codeBlock.beginControlFlow("putIndex(new $T($T.class, new $T[]",
                        EventBeans.class,
                        ClassName.get(entry.getKey()),
                        SubscriberMethod.class)
                        // 嵌套的精华（尝试了很多次，有更好的方式请告诉我）
                        .add(contentCode)
                        // ))}
                        .endControlFlow("))");
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "注解处理器双层循环发生错误！");
            }
        }

        // 全局属性：Map<Class<?>, SubscriberMethod>
        TypeName fieldType = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(Class.class), // Map<Class,
                ClassName.get(SubscriberInfo.class) // Map<Class, SubscriberMethod>
        );

        // putIndex方法参数：putIndex(SubscriberInfo info)
        ParameterSpec putIndexParameter = ParameterSpec.builder(
                ClassName.get(SubscriberInfo.class),
                Constants.PUTINDEX_PARAMETER_NAME)
                .build();

        // putIndex方法配置：private static void putIndex(SubscriberMethod info) {
        MethodSpec.Builder putIndexBuidler = MethodSpec
                .methodBuilder(Constants.PUTINDEX_METHOD_NAME) // 方法名
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC) // private static修饰符
                .addParameter(putIndexParameter); // 添加方法参数
        // 不填returns默认void返回值

        // putIndex方法内容：SUBSCRIBER_INDEX.put(info.getSubscriberClass(), info);
        putIndexBuidler.addStatement("$N.put($N.getSubscriberClass(), $N)",
                Constants.FIELD_NAME,
                Constants.PUTINDEX_PARAMETER_NAME,
                Constants.PUTINDEX_PARAMETER_NAME);

        // getSubscriberInfo方法参数：Class subscriberClass
        ParameterSpec getSubscriberInfoParameter = ParameterSpec.builder(
                ClassName.get(Class.class),
                Constants.GETSUBSCRIBERINFO_PARAMETER_NAME)
                .build();

        // getSubscriberInfo方法配置：public SubscriberMethod getSubscriberInfo(Class<?> subscriberClass) {
        MethodSpec.Builder getSubscriberInfoBuidler = MethodSpec
                .methodBuilder(Constants.GETSUBSCRIBERINFO_METHOD_NAME) // 方法名
                .addAnnotation(Override.class) // 重写方法注解
                .addModifiers(Modifier.PUBLIC) // public修饰符
                .addParameter(getSubscriberInfoParameter) // 方法参数
                .returns(SubscriberInfo.class); // 方法返回值

        // getSubscriberInfo方法内容：return SUBSCRIBER_INDEX.get(subscriberClass);
        getSubscriberInfoBuidler.addStatement("return $N.get($N)",
                Constants.FIELD_NAME,
                Constants.GETSUBSCRIBERINFO_PARAMETER_NAME);

        // 构建类
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                // 实现SubscriberInfoIndex接口
                .addSuperinterface(ClassName.get(subscriberIndexType))
                // 该类的修饰符
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                // 添加静态块（很少用的api）
                .addStaticBlock(codeBlock.build())
                // 全局属性：private static final Map<Class<?>, SubscriberMethod> SUBSCRIBER_INDEX
                .addField(fieldType, Constants.FIELD_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                // 第一个方法：加入全局Map集合
                .addMethod(putIndexBuidler.build())
                // 第二个方法：通过订阅者对象（MainActivity.class）获取所有订阅方法
                .addMethod(getSubscriberInfoBuidler.build())
                .build();

        // 生成类文件：EventBusIndex
        JavaFile.builder(packageName, // 包名
                typeSpec) // 类构建完成
                .build() // JavaFile构建完成
                .writeTo(filer); // 文件生成器开始生成类文件
    }

    /**
     * 检查方法，条件：订阅方法必须是非静态的，公开的，参数只能有一个
     *
     * @param element 方法元素
     * @return 检查是否通过
     */
    private boolean checkHasNoErrors(ExecutableElement element) {
        // 不能为static静态方法
        if (element.getModifiers().contains(Modifier.STATIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "订阅事件方法不能是static静态方法", element);
            return false;
        }

        // 必须是public修饰的方法
        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "订阅事件方法必须是public修饰的方法", element);
            return false;
        }

        // 订阅事件方法必须只有一个参数
        List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
        if (parameters.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "订阅事件方法有且仅有一个参数", element);
            return false;
        }
        return true;
    }

}
