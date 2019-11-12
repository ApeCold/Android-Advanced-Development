package com.netease.apt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.netease.annotation.ARouter;

@ARouter(path = "/app/OrderActivity")
public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("netease >>> ", "----------> OrderActivity");
    }

    public void jump(View view) {
        Intent intent = new Intent(this, PersonalActivity$$ARouter.findTargetClass("/app/PersonalActivity"));
        startActivity(intent);
    }
}
