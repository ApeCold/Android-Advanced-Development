package com.netease.plugin_package;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PluginActivity extends BaseActivity {

    private final String ACTION = "com.netease.plugin_package.ACTION";

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

        // 注册广播
        findViewById(R.id.bt_register_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION);
                registerReceiver(new MyReceiver(), intentFilter);
            }
        });

        // 发送广播
        findViewById(R.id.bt_send_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(ACTION);
                sendBroadcast(intent);
            }
        });
    }

}
