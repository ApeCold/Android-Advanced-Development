package com.netease.butterknife.compiler;

import com.google.auto.service.AutoService;
import com.netease.butterknife.annotation.BindView;
import com.netease.butterknife.annotation.OnClick;
import com.netease.butterknife.compiler.utils.Constants;
import com.netease.butterknife.compiler.utils.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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

// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.BINDVIEW_ANNOTATION_TYPES, Constants.ONCLICK_ANNOTATION_TYPES})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ButterKnifeProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的类文件，class文件以及辅助文件
    private Filer filer;

    // key:类节点, value:被@BindView注解的属性集合
    private Map<TypeElement, List<VariableElement>> tempBindViewMap = new HashMap<>();

    // key:类节点, value:被@OnClick注解的方法集合
    private Map<TypeElement, List<ExecutableElement>> tempOnClickMap = new HashMap<>();

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 初始化
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE,
                "注解处理器初始化完成，开始处理注解------------------------------->");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        // 一旦属性上使用了@BindView注解
        if (!EmptyUtils.isEmpty(set)) {
            // 获取所有被 @BindView注解的 属性元素集合
            Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(BindView.class);

            // 获取所有被 @OnClick注解的 方法元素集合
            Set<? extends Element> onClickElements = roundEnv.getElementsAnnotatedWith(OnClick.class);

            if (!EmptyUtils.isEmpty(bindViewElements) || !EmptyUtils.isEmpty(onClickElements)) {
                // 收集信息，存储到temp集合中。用来生成代码
                valueOfMap(bindViewElements, onClickElements);

                try {
                    // 生成类文件
                    createJavaFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 收集信息，存储Map赋值
     */
    private void valueOfMap(Set<? extends Element> bindViewElements, Set<? extends Element> onClickElements) {
        if (!EmptyUtils.isEmpty(bindViewElements)) {
            for (Element element : bindViewElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, "@BindView >> " + element.getSimpleName());
                if (element.getKind() == ElementKind.FIELD) {
                    VariableElement fieldElement = (VariableElement) element;
                    // 属性节点，上一个（父节点），类节点
                    TypeElement typeElement = (TypeElement) fieldElement.getEnclosingElement();
                    // 如果map集合中包含了key（类节点）
                    if (tempBindViewMap.containsKey(typeElement)) {
                        tempBindViewMap.get(typeElement).add(fieldElement);
                    } else {
                        List<VariableElement> fields = new ArrayList<>();
                        fields.add(fieldElement);
                        tempBindViewMap.put(typeElement, fields);
                    }
                }
            }
        }

        if (!EmptyUtils.isEmpty(onClickElements)) {
            for (Element element : onClickElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, "@OnClick >> " + element.getSimpleName());
                if (element.getKind() == ElementKind.METHOD) {
                    ExecutableElement methodElement = (ExecutableElement) element;
                    // 属性节点，上一个（父节点），类节点
                    TypeElement typeElement = (TypeElement) methodElement.getEnclosingElement();
                    // 如果map集合中包含了key（类节点）
                    if (tempOnClickMap.containsKey(typeElement)) {
                        tempOnClickMap.get(typeElement).add(methodElement);
                    } else {
                        List<ExecutableElement> methods = new ArrayList<>();
                        methods.add(methodElement);
                        tempOnClickMap.put(typeElement, methods);
                    }
                }
            }
        }
    }

    private void createJavaFile() throws IOException {
        // 判断是否有需要生成的类文件（有坑）
        if (!EmptyUtils.isEmpty(tempBindViewMap)) {

            // 获取接口的类型
            TypeElement viewBinderType = elementUtils.getTypeElement(Constants.VIEWBINDER);
            TypeElement clickListenerType = elementUtils.getTypeElement(Constants.CLICKLISTENER);
            TypeElement viewType = elementUtils.getTypeElement(Constants.VIEW);

            // 从下往上写（JavaPoet技巧）
            for (Map.Entry<TypeElement, List<VariableElement>> entry : tempBindViewMap.entrySet()) {

                // 类名（TypeElement）
                ClassName className = ClassName.get(entry.getKey());
                // 实现接口泛型（implements ViewBinder<MainActivity>）
                ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(viewBinderType),
                        ClassName.get(entry.getKey()));

                // 方法参数体
                ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get(entry.getKey()), // MainActivity
                        Constants.TARGET_PARAMETER_NAME) // 方法参数名target
                        .addModifiers(Modifier.FINAL) // 参数修饰符
                        .build(); // 参数体构建完成

                // 方法体：public void bind(final MainActivity target) {
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.BIND_METHOD_NAME) // bind方法名
                        .addAnnotation(Override.class) // 接口重写方法
                        .addModifiers(Modifier.PUBLIC) // 方法修饰符
                        .addParameter(parameterSpec); // 方法参数

                for (VariableElement fieldElement : entry.getValue()) {
                    // 获取属性名
                    String fieldName = fieldElement.getSimpleName().toString();
                    // 获取@BindView注解的值
                    int annotationValue = fieldElement.getAnnotation(BindView.class).value();
                    // target.tv = target.findViewById(R.id.tv);
                    String methodContent = "$N." + fieldName + " = $N.findViewById($L)";
                    // 加入方法内容
                    methodBuilder.addStatement(methodContent,
                            Constants.TARGET_PARAMETER_NAME, // target
                            Constants.TARGET_PARAMETER_NAME,  // target
                            annotationValue); // R.id.xx
                }

                if (!EmptyUtils.isEmpty(tempOnClickMap)) {
                    for (Map.Entry<TypeElement, List<ExecutableElement>> methodEntry : tempOnClickMap.entrySet()) {
                        // 类名
                        if (className.equals(ClassName.get(methodEntry.getKey()))) {
                            for (ExecutableElement methodElement : methodEntry.getValue()) {
                                // 获取方法名
                                String methodName = methodElement.getSimpleName().toString();
                                // 获取@OnClick注解的值
                                int annotationValue = methodElement.getAnnotation(OnClick.class).value();

                                /**
                                 * target.findViewById(R.id.tv).setOnClickListener(new DebouncingOnClickListener() {
                                 *
                                 *             @Override
                                 *             public void doClick(View v) {
                                 *                 target.click(v);
                                 *             }
                                 *         });
                                 */
                                methodBuilder.beginControlFlow("$N.findViewById($L).setOnClickListener(new $T()",
                                        Constants.TARGET_PARAMETER_NAME, annotationValue, ClassName.get(clickListenerType))
                                        .beginControlFlow("public void doClick($T v)", ClassName.get(viewType))
                                        .addStatement("$N." + methodName + "(v)", Constants.TARGET_PARAMETER_NAME)
                                        .endControlFlow()
                                        .endControlFlow(")")
                                        .build();
                            }
                        }
                    }
                }

                // 生成必须是同包：（属性的修饰符是缺失的）
                JavaFile.builder(className.packageName(),  // 包名
                        TypeSpec.classBuilder(className.simpleName() + "$ViewBinder") // 类名
                                .addSuperinterface(typeName) // 实现ViewBinder接口（有泛型）
                                .addModifiers(Modifier.PUBLIC) // 类修饰符
                                .addMethod(methodBuilder.build()) // 加入方法体
                                .build()) // 类构建完成
                        .build() // JavaFile构建
                        .writeTo(filer); // 文件生成器开始生成类文件
            }
        }
    }
}
