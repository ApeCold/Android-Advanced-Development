package com.netease.javapoet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.netease.annotation.ARouter;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jump(View view) {
        Intent intent = new Intent(this, OrderActivity$$ARouter.findTargetClass("/app/OrderActivity"));
        startActivity(intent);
    }
}
