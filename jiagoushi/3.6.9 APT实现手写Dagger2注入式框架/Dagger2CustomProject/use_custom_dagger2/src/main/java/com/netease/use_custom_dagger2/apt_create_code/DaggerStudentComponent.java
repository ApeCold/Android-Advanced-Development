package com.netease.use_custom_dagger2.apt_create_code;

import com.netease.custom_dagger2.DoubleCheck;
import com.netease.custom_dagger2.MembersInjctor;
import com.netease.custom_dagger2.Provider;
import com.netease.use_custom_dagger2.MainActivity;
import com.netease.use_custom_dagger2.Student;
import com.netease.use_custom_dagger2.StudentComponent;
import com.netease.use_custom_dagger2.StudentModule;

// TODO 这个是编译期 APT 自动生成的  // 第三个注解
public class DaggerStudentComponent implements StudentComponent {

    public DaggerStudentComponent(Builder builder) {
        initialize(builder); // 我们写第四个注解，才会生成
    }

    // 我们写第四个注解，才会生成
    private Provider<Student> studentProvider;
    private MembersInjctor<MainActivity> mainActivityMembersInjctor;

    private void initialize(Builder builder) {
        studentProvider =
                DoubleCheck.provider(StudentModule_ProviderStudentFactory.create(builder.studentModule));
        mainActivityMembersInjctor = MainActivity_MembersInjector.create(studentProvider);
    }
    // ------------------------

    public static Builder builder() {
        return new Builder();
    }

    private final static class Builder {

        StudentModule studentModule; // 定义了包裹{Student ---> MainActivity}

        private Builder() { }

        public StudentComponent build() {
            if (studentModule == null) {
                studentModule = new StudentModule();
            }
            return new DaggerStudentComponent(this);
        }

    }

    // 对外提供Builder
    public static StudentComponent create() {
        return builder().build();
    }

    // 往目标（MainActivity）中去注入
    @Override
    public void inject(MainActivity mainActivity) {
        mainActivityMembersInjctor.injectMembers(mainActivity);
    }
}
