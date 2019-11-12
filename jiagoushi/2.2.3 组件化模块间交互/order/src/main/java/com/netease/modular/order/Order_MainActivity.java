package com.netease.modular.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.common.RecordPathManager;
import com.netease.common.base.BaseActivity;
import com.netease.common.utils.Cons;

public class Order_MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        Log.e(Cons.TAG, "order/Order_MainActivity");

        if (getIntent() != null) {
            String content = getIntent().getStringExtra("name");
            Log.e(Cons.TAG, "接收参数值：" + content);
        }
    }

    public void jumpApp(View view) {
        // 类加载跳转，可以成功。维护成本较高且容易出现人为失误
//        try {
//            Class targetClass = Class.forName("com.netease.modular.MainActivity");
//            Intent intent = new Intent(this, targetClass);
//            intent.putExtra("name", "simon");
//            startActivity(intent);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


        Class<?> targetClass = RecordPathManager.getTargetClass("app", "MainActivity");

        if (targetClass == null) {
            Log.e(Cons.TAG, "获取跳转targetClass失败！");
            return;
        }

        Intent intent = new Intent(this, targetClass);
        intent.putExtra("name", "simon");
        startActivity(intent);
    }

    public void jumpPersonal(View view) {
//        try {
//            Class targetClass = Class.forName("com.netease.modular.personal.Personal_MainActivity");
//            Intent intent = new Intent(this, targetClass);
//            intent.putExtra("name", "simon");
//            startActivity(intent);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


        Class<?> targetClass = RecordPathManager.getTargetClass("personal", "Personal_MainActivity");

        if (targetClass == null) {
            Log.e(Cons.TAG, "获取跳转targetClass失败！");
            return;
        }

        Intent intent = new Intent(this, targetClass);
        intent.putExtra("name", "simon");
        startActivity(intent);
    }
}
