package com.netease.apt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.netease.annotation.ARouter;

@ARouter(path = "/app/PersonalActivity")
public class PersonalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("netease >>> ", "----------> PersonalActivity");
    }

    public void jump(View view) {
    }
}
