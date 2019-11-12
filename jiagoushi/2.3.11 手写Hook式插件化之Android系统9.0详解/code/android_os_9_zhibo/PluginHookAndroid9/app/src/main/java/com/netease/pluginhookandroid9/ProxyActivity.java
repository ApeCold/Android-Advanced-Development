package com.netease.pluginhookandroid9;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 代理Activity,此Activity存在的目的是为了过安检(AMS检查)
 */
public class ProxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "我是代理Activity....", Toast.LENGTH_SHORT).show();
    }
}
