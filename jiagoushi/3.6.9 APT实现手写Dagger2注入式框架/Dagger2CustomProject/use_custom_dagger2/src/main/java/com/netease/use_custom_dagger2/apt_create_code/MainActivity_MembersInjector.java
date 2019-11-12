package com.netease.use_custom_dagger2.apt_create_code;

import com.netease.custom_dagger2.MembersInjctor;
import com.netease.custom_dagger2.Provider;
import com.netease.use_custom_dagger2.MainActivity;
import com.netease.use_custom_dagger2.Student;

// TODO 这个是编译期 APT 自动生成的  真正完成依赖注入的  // 第四个注解
// 只有在目标（MainActivity）去注入Student对象的时候，在编译时期才会去生成此类
public class MainActivity_MembersInjector implements MembersInjctor<MainActivity> {

    private Provider<Student> studentProvider; // 定义最高标准，为了拿到 对象的实例化

    public MainActivity_MembersInjector(Provider<Student> studentProvider) {
        this.studentProvider = studentProvider;
    }

    // 提供一个静态创建|的方法
    public static MainActivity_MembersInjector create(Provider<Student> studentProvider) {
        return new MainActivity_MembersInjector(studentProvider);
    }

    @Override  // 注入的方法
    public void injectMembers(MainActivity instance) { // instanc == MainActivity this
        if (null == instance) {
            throw new NullPointerException("inject target instance is null");
        }
        instance.student = studentProvider.get(); // MainActivity this.student = new Student();
        instance.student2 = studentProvider.get();
    }
}
