package com.netease.ndk_2_3_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    {
        System.loadLibrary("hello-jni");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_msg =  findViewById(R.id.tv_msg);
        tv_msg.setText("nativeTest: "+nativeTest());
    }

    native int nativeTest();
}
