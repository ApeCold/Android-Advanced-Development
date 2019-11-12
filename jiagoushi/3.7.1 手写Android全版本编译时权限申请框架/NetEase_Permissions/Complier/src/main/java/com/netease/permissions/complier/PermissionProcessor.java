package com.netease.permissions.complier;

import com.google.auto.service.AutoService;
import com.netease.permissions.annotations.NeedsPermission;
import com.netease.permissions.annotations.OnNeverAskAgain;
import com.netease.permissions.annotations.OnPermissionDenied;
import com.netease.permissions.annotations.OnShowRationale;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器是Google开发的，
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// @SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PermissionProcessor extends AbstractProcessor {

    private Messager messager; // Messager用来报告错误，警告和其他提示信息
    private Elements elementUtils; // Elements中包含用于操作Element的工具方法
    private Filer filer; // Filter用来创建新的源文件，class文件以及辅助文件
    private Types typeUtils; // Types中包含用于操作TypeMirror的工具方法

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        // 添加支持注解的类型
        types.add(NeedsPermission.class.getCanonicalName());
        types.add(OnNeverAskAgain.class.getCanonicalName());
        types.add(OnPermissionDenied.class.getCanonicalName());
        types.add(OnShowRationale.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // 返回此注释 Processor 支持的最新的源版本，该方法可以通过注解@SupportedSourceVersion指定
        return SourceVersion.latest();
    }

    // 注解处理器的核心方法，处理具体的注解，生成Java文件
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // 获取MainActivity中所有带NeedsPermission注解的方法
        Set<? extends Element> needsPermissionSet = roundEnvironment.getElementsAnnotatedWith(NeedsPermission.class);
        // 保存键值对，key是com.netease.permission.MainActivity   value是所有带NeedsPermission注解的方法集合
        Map<String, List<ExecutableElement>> needsPermissionMap = new HashMap<>();
        // 遍历所有带NeedsPermission注解的方法
        for (Element element : needsPermissionSet) {
            // 转成原始属性元素（结构体元素）
            ExecutableElement executableElement = (ExecutableElement) element;
            // 通过属性元素获取它所属的MainActivity类名，如：com.netease.permission.MainActivity
            String activityName = getActivityName(executableElement);
            // 从缓存集合中获取MainActivity所有带NeedsPermission注解的方法集合
            List<ExecutableElement> list = needsPermissionMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                // 先加入map集合，引用变量list可以动态改变值
                needsPermissionMap.put(activityName, list);
            }
            // 将MainActivity所有带NeedsPermission注解的方法加入到list集合
            list.add(executableElement);
            // 测试打印：每个方法的名字
            System.out.println("NeedsPermission executableElement >>> " + executableElement.getSimpleName().toString());
        }

        // 获取MainActivity中所有带OnNeverAskAgain注解的方法
        Set<? extends Element> onNeverAskAgainSet = roundEnvironment.getElementsAnnotatedWith(OnNeverAskAgain.class);
        Map<String, List<ExecutableElement>> onNeverAskAgainMap = new HashMap<>();
        for (Element element : onNeverAskAgainSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);
            List<ExecutableElement> list = onNeverAskAgainMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                onNeverAskAgainMap.put(activityName, list);
            }
            list.add(executableElement);
            System.out.println("executableElement >>> " + executableElement.getSimpleName().toString());
        }

        // 获取MainActivity中所有带OnPermissionDenied注解的方法
        Set<? extends Element> onPermissionDeniedSet = roundEnvironment.getElementsAnnotatedWith(OnPermissionDenied.class);
        Map<String, List<ExecutableElement>> onPermissionDeniedMap = new HashMap<>();
        for (Element element : onPermissionDeniedSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);
            List<ExecutableElement> list = onPermissionDeniedMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                onPermissionDeniedMap.put(activityName, list);
            }
            list.add(executableElement);
            System.out.println("executableElement >>> " + executableElement.getSimpleName().toString());
        }

        // 获取MainActivity中所有带OnShowRationale注解的方法
        Set<? extends Element> onShowRationaleMapSet = roundEnvironment.getElementsAnnotatedWith(OnShowRationale.class);
        Map<String, List<ExecutableElement>> onShowRationaleMap = new HashMap<>();
        for (Element element : onShowRationaleMapSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String activityName = getActivityName(executableElement);
            List<ExecutableElement> list = onShowRationaleMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                onShowRationaleMap.put(activityName, list);
            }
            list.add(executableElement);
            System.out.println("executableElement >>> " + executableElement.getSimpleName().toString());
        }

        //----------------------------------造币过程------------------------------------
        // 获取Activity完整的字符串类名（包名 + 类名）
        for (String activityName : needsPermissionMap.keySet()) {
            // 获取"com.netease.permission.MainActivity"中所有控件方法的集合
            List<ExecutableElement> needsPermissionElements = needsPermissionMap.get(activityName);
            List<ExecutableElement> onNeverAskAgainElements = onNeverAskAgainMap.get(activityName);
            List<ExecutableElement> onPermissionDeniedElements = onPermissionDeniedMap.get(activityName);
            List<ExecutableElement> onShowRationaleElements = onShowRationaleMap.get(activityName);

            final String CLASS_SUFFIX = "$Permissions";
            Filer filer = processingEnv.getFiler();
            try {
                // 创建一个新的源文件（Class），并返回一个对象以允许写入它
                JavaFileObject javaFileObject = filer.createSourceFile(activityName + CLASS_SUFFIX);
                // 通过方法标签获取包名标签（任意一个属性标签的父节点都是同一个包名）
                String packageName = getPackageName(needsPermissionElements.get(0));
                // 定义Writer对象，开启造币过程
                Writer writer = javaFileObject.openWriter();

                // 类名：MainActivity$Permissions，不是com.netease.permission.MainActivity$Permissions
                // 通过属性元素获取它所属的MainActivity类名，再拼接后结果为：MainActivity$Permissions
                String activitySimpleName = needsPermissionElements.get(0).getEnclosingElement()
                        .getSimpleName().toString() + CLASS_SUFFIX;

                System.out.println("activityName >>> " + activityName + "\nactivitySimpleName >>> " + activitySimpleName);

                System.out.println("开始造币 ----------------------------------->");
                // 生成包
                writer.write("package " + packageName + ";\n");
                // 生成要导入的接口类（必须手动导入）
                writer.write("import com.netease.permissions.library.listener.RequestPermission;\n");
                writer.write("import com.netease.permissions.library.listener.PermissionRequest;\n");
                writer.write("import com.netease.permissions.library.utils.PermissionUtils;\n");
                writer.write("import android.support.v7.app.AppCompatActivity;\n");
                writer.write("import android.support.v4.app.ActivityCompat;\n");
                writer.write("import android.support.annotation.NonNull;\n");
                writer.write("import java.lang.ref.WeakReference;\n");

                // 生成类
                writer.write("public class " + activitySimpleName +
                        " implements RequestPermission<" + activityName + "> {\n");

                // 生成常量属性
                writer.write("private static final int REQUEST_SHOWCAMERA = 666;\n");
                writer.write("private static String[] PERMISSION_SHOWCAMERA;\n");

                // 生成requestPermission方法
                writer.write("public void requestPermission(" + activityName + " target, String[] permissions) {\n");

                writer.write("PERMISSION_SHOWCAMERA = permissions;\n");
                writer.write("if (PermissionUtils.hasSelfPermissions(target, PERMISSION_SHOWCAMERA)) {\n");

                // 循环生成MainActivity每个权限申请方法
                for (ExecutableElement executableElement : needsPermissionElements) {
                    // 获取方法名
                    String methodName = executableElement.getSimpleName().toString();
                    // 调用申请权限方法
                    writer.write("target." + methodName + "();\n");
                }

                writer.write("} else if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {\n");

                // 循环生成MainActivity每个提示用户为何要开启权限方法
                if (onShowRationaleElements != null && !onShowRationaleElements.isEmpty()) {
                    for (ExecutableElement executableElement : onShowRationaleElements) {
                        // 获取方法名
                        String methodName = executableElement.getSimpleName().toString();
                        // 调用提示用户为何要开启权限方法
                        writer.write("target." + methodName + "(new PermissionRequestImpl(target));\n");
                    }
                }

                writer.write("} else {\n");
                writer.write("ActivityCompat.requestPermissions(target, PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);\n}\n}\n");

                // 生成onRequestPermissionsResult方法
                writer.write("public void onRequestPermissionsResult(" + activityName + " target, int requestCode, @NonNull int[] grantResults) {");
                writer.write("switch(requestCode) {\n");
                writer.write("case REQUEST_SHOWCAMERA:\n");
                writer.write("if (PermissionUtils.verifyPermissions(grantResults)) {\n");

                // 循环生成MainActivity每个权限申请方法
                for (ExecutableElement executableElement : needsPermissionElements) {
                    // 获取方法名
                    String methodName = executableElement.getSimpleName().toString();
                    // 调用申请权限方法
                    writer.write("target." + methodName + "();\n");
                }

                writer.write("} else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {\n");

                // 循环生成MainActivity每个不再询问后的提示
                if (onNeverAskAgainElements != null && !onNeverAskAgainElements.isEmpty()) {
                    for (ExecutableElement executableElement : onNeverAskAgainElements) {
                        // 获取方法名
                        String methodName = executableElement.getSimpleName().toString();
                        // 调用不再询问后的提示
                        writer.write("target." + methodName + "();\n");
                    }
                }

                writer.write("} else {\n");

                // 循环生成MainActivity每个拒绝时的提示方法
                if (onPermissionDeniedElements != null && !onPermissionDeniedElements.isEmpty()) {
                    for (ExecutableElement executableElement : onPermissionDeniedElements) {
                        // 获取方法名
                        String methodName = executableElement.getSimpleName().toString();
                        // 调用拒绝时的提示方法
                        writer.write("target." + methodName + "();\n");
                    }
                }

                writer.write("}\nbreak;\ndefault:\nbreak;\n}\n}\n");

                // 生成接口实现类：PermissionRequestImpl implements PermissionRequest
                writer.write("private static final class PermissionRequestImpl implements PermissionRequest {\n");
                writer.write("private final WeakReference<" + activityName + "> weakTarget;\n");
                writer.write("private PermissionRequestImpl(" + activityName + " target) {\n");
                writer.write("this.weakTarget = new WeakReference(target);\n}\n");
                writer.write("public void proceed() {\n");
                writer.write(activityName + " target = (" + activityName + ")this.weakTarget.get();\n");
                writer.write("if (target != null) {\n");
                writer.write("ActivityCompat.requestPermissions(target, PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);\n}\n}\n}\n");

                // 最后结束标签，造币完成
                writer.write("\n}");
                System.out.println("结束 ----------------------------------->");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private String getActivityName(ExecutableElement executableElement) {
        // 通过方法标签获取类名标签，再通过类名标签获取包名标签
        String packageName = getPackageName(executableElement);
        // 通过方法标签获取类名标签
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        // 完整字符串拼接：com.netease.permission + "." + MainActivity
        return packageName + "." + typeElement.getSimpleName().toString();
    }

    private String getPackageName(ExecutableElement executableElement) {
        // 通过方法标签获取类名标签
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        // 通过类名标签获取包名标签
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        System.out.println("packageName >>>  " + packageName);
        return packageName;
    }
}
