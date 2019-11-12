package com.netease.hookproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

// 必须要在AndroidManifest注册，为什么，因为此Activity是需要通过 AMS 检查的
public class ProxyActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "我是代理的Activity", Toast.LENGTH_SHORT).show();
    }
}
