package com.netease.plugin_package;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PluginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_main);

        // this 会报错，因为插件没有安装，也没有组件的环境，所以必须使用宿主环境

        Toast.makeText(appActivity, "我是插件", Toast.LENGTH_SHORT).show();

        findViewById(R.id.bt_start_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(appActivity, TestActivity.class));
            }
        });

        findViewById(R.id.bt_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(appActivity, TestService.class));
            }
        });
    }

}
