package com.netease.archtect.facade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// 外观模式（门面模式）
// 隐藏了系统的复杂性，为子系统中的一组接口提供了一个统一的访问接口
// 高内聚、低耦合

// 模拟场景：图片加载
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
