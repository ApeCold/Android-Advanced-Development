package com.netease.newplugin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 启动LoginActivity ----》 AMS（检查是否注册了） --》真正的实例化
     * @param view
     */
    public void start(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
