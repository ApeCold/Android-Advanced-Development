package com.netease.eventbus.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 未初始化 / 延时消费
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    // 点击事件
    public void post(View view) {
        // 发布事件
        // EventBus.getDefault().post("simon");
        EventBus.getDefault().register(this);
    }

    // 测试粘性事件
    @Subscribe(sticky = true)
    public void sticky(String string) {
        Log.e("sticky >>> ", string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
