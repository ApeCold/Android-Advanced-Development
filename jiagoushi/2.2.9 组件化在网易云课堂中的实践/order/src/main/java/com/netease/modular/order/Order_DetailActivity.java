package com.netease.modular.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.netease.arouter.annotation.ARouter;
import com.netease.arouter.annotation.Parameter;
import com.netease.arouter.api.ParameterManager;
import com.netease.common.base.BaseActivity;
import com.netease.common.utils.Cons;

@ARouter(path = "/order/Order_DetailActivity")
public class Order_DetailActivity extends BaseActivity {

    @Parameter
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_activity);

        Log.e(Cons.TAG, "order/Order_DetailActivity");

        // 懒加载方式，跳到哪加载哪个类
        ParameterManager.getInstance().loadParameter(this);

        Log.e(Cons.TAG, "接收参数值：" + username);
    }
}
