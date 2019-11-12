package com.netease.dagger2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Inject
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // student = new Student();

        //  把自己MainActivity--this 交给 Dagger2
        DaggerStudentComponent.create().injectMainActivity(this); // this MainActivity


        Log.d(TAG, "onCreate: studnet.hashCode():" + student.hashCode() + "     student.name:" + student.name);
    }
}
