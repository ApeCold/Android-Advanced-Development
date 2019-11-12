package com.netease.use_custom_dagger2.apt_create_code;

import com.netease.custom_dagger2.Factory;
import com.netease.custom_dagger2.Preconditions;
import com.netease.use_custom_dagger2.Student;
import com.netease.use_custom_dagger2.StudentModule;

// TODO 这个是编译期 APT 自动生成的  // 第二个注解
public class StudentModule_ProviderStudentFactory implements Factory<Student> {

    private StudentModule studentModule;

    public StudentModule_ProviderStudentFactory(StudentModule studentModule) {
        this.studentModule = studentModule;
    }

    @Override
    public Student get() {
        //  studentModule.providerStudent() == new Student();
        return Preconditions.checkNotNull(studentModule.providerStudent(),
                "studentModule.providerStudent() is null exception...");
    }

    // 额外生成一个方法，为后续提供
    public static Factory<Student> create(StudentModule studentModule) {
        return new StudentModule_ProviderStudentFactory(studentModule);
    }
}
