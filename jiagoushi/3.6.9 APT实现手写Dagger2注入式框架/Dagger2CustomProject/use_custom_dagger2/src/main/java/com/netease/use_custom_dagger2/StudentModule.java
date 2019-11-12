package com.netease.use_custom_dagger2;

import com.netease.custom_dagger2.ann.Module;
import com.netease.custom_dagger2.ann.Provides;

@Module // 第二个注解 // 当我们写 Module时，编译过程中，不会生成代码
public class StudentModule { // 包裹

    @Provides // 第二个注解
    public Student providerStudent() {
        return new Student();
    }

}
