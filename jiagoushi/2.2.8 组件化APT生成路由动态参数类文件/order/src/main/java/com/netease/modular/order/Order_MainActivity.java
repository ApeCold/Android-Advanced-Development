package com.netease.modular.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.arouter.annotation.ARouter;
import com.netease.arouter.annotation.Parameter;
import com.netease.common.base.BaseActivity;
import com.netease.common.utils.Cons;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends BaseActivity {

    @Parameter
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        Log.e(Cons.TAG, "order/Order_MainActivity");

        if (getIntent() != null) {
            // String content = getIntent().getStringExtra("name");
            // Log.e(Cons.TAG, "接收参数值：" + content);

            new Order_MainActivity$$Parameter().loadParameter(this);
            Log.e(Cons.TAG, "接收参数值：" + name);
        }
    }

    public void jumpApp(View view) {

    }

    public void jumpPersonal(View view) {

    }
}
