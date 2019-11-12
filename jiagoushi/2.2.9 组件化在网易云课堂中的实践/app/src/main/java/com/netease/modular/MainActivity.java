package com.netease.modular;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.netease.arouter.annotation.ARouter;
import com.netease.arouter.annotation.Parameter;
import com.netease.arouter.api.ParameterManager;
import com.netease.arouter.api.RouterManager;
import com.netease.common.base.BaseActivity;
import com.netease.common.order.OrderAddress;
import com.netease.common.order.OrderBean;
import com.netease.common.order.drawable.OrderDrawable;
import com.netease.common.utils.Cons;

import java.io.IOException;

// 小项目或者子模块类重复率不高，可以直接写：path = "/MainActivity"
@ARouter(path = "/app/MainActivity")
public class MainActivity extends BaseActivity {

    @Parameter
    String name;

    @Parameter
    int age;

    @Parameter
    boolean isSuccess;

    @Parameter(name = "netease")
    String object;

    @Parameter(name = "/order/getOrderBean")
    OrderAddress orderAddress;

    @Parameter(name = "/order/getDrawable")
    OrderDrawable orderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.isRelease) {
            Log.e(Cons.TAG, "当前为：集成化模式，除app可运行，其他子模块都是Android Library");
        } else {
            Log.e(Cons.TAG, "当前为：组件化模式，app/order/personal子模块都可独立运行");
        }

        // 懒加载方式，跳到哪加载哪个类
        ParameterManager.getInstance().loadParameter(this);

        // 测试接收传递参数
        Log.e(Cons.TAG, toString());

        int drawableId = orderDrawable.getDrawable();
        ImageView img = findViewById(R.id.img);
        img.setImageResource(drawableId);

        // 测试获取接口通信
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderBean orderBean = orderAddress.getOrderBean("aa205eeb45aa76c6afe3c52151b52160", "144.34.161.97");
                    Log.e("netease >>> ", orderBean.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void jumpOrder(View view) {
        RouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .withString("username", "simon")
                .navigation(this, 163);
    }

    public void jumpPersonal(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("name", "simon");
        bundle.putInt("age", 35);
        bundle.putBoolean("isSuccess", true);
        bundle.putString("netease", "net163");

        RouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .withString("username", "baby")
                .withBundle(bundle)
                .navigation(this, 163);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.e(Cons.TAG, data.getStringExtra("call"));
        }
    }

    @Override
    public String toString() {
        return "MainActivity{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isSuccess=" + isSuccess +
                ", object='" + object + '\'' +
                '}';
    }
}
