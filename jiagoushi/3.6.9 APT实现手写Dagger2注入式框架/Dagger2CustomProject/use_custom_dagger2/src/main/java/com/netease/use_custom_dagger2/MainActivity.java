package com.netease.use_custom_dagger2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.netease.custom_dagger2.Provider;
import com.netease.custom_dagger2.ann.Inject;
import com.netease.use_custom_dagger2.apt_create_code.DaggerStudentComponent;

// 目标对象（MainActivity）
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    public Student student; // 第四个注解

    /*@Inject
    public Student student2;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 方法进栈
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerStudentComponent.create().inject(this);

        Log.d(TAG, "onCreate: " + student.hashCode() + " --- "/* + student2.hashCode()*/);

        return; // 方法弹栈  gc可以去回收Dagger里面的关系
    }
}
