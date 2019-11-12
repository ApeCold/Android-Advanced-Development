package com.netease.pluginhookandroid9;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 宿主主Activity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 宿主启动宿主中的LoginActivity
     * @param view
     */
    public void startMainLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 宿主启动[插件]中的PluginLoginActivity
     * @param view
     */
    public void startPluginLoginActivity(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.netease.plugin", "com.netease.plugin.PluginLoginActivity"));
        startActivity(intent);
    }
}
