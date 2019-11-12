package com.netease.modular.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.arouter.annotation.ARouter;
import com.netease.arouter.annotation.Parameter;
import com.netease.arouter.api.ParameterManager;
import com.netease.arouter.api.RouterManager;
import com.netease.common.base.BaseActivity;
import com.netease.common.user.BaseUser;
import com.netease.common.user.IUser;
import com.netease.common.utils.Cons;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends BaseActivity {

    @Parameter(name = "/app/getUserInfo")
    IUser iUser;

    @Parameter
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        Log.e(Cons.TAG, "order/Order_MainActivity");

        // 懒加载方式，跳到哪加载哪个类
        ParameterManager.getInstance().loadParameter(this);

        Log.e(Cons.TAG, "接收参数值：" + username);

        BaseUser userInfo = iUser.getUserInfo();
        if (userInfo != null) {
            Log.e(Cons.TAG, userInfo.getName() + " / "
                    + userInfo.getAccount() + " / "
                    + userInfo.getPassword());
        }
    }

    public void jumpApp(View view) {
        RouterManager.getInstance()
                .build("/app/MainActivity")
                .withResultString("call", "I'am comeback!")
                .navigation(this);
    }

    public void jumpPersonal(View view) {
        RouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .withString("name", "simon")
                .navigation(this);
    }
}
