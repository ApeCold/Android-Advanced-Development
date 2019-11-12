package com.netease.modular;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.arouter.annotation.model.RouterBean;
import com.netease.arouter.api.core.ARouterLoadGroup;
import com.netease.arouter.api.core.ARouterLoadPath;
import com.netease.common.base.BaseActivity;
import com.netease.common.utils.Cons;
import com.netease.modular.personal.Personal_MainActivity;
import com.netease.modular.test.ARouter$$Group$$order;
import com.netease.modular.test.ARouter$$Path$$order;

import java.util.Map;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.isRelease) {
            Log.e(Cons.TAG, "当前为：集成化模式，除app可运行，其他子模块都是Android Library");
        } else {
            Log.e(Cons.TAG, "当前为：组件化模式，app/order/personal子模块都可独立运行");
        }
    }

    public void jumpOrder(View view) {
//        Intent intent = new Intent(this, Order_MainActivity.class);
//        intent.putExtra("name", "simon");
//        startActivity(intent);

        // 最终集成化模式，所有子模块app/order/personal通过APT生成的类文件都会打包到apk里面，不用担心找不到
        ARouterLoadGroup group = new ARouter$$Group$$order();
        Map<String, Class<? extends ARouterLoadPath>> map = group.loadGroup();
        // 通过order组名获取对应路由路径对象
        Class<? extends ARouterLoadPath> clazz = map.get("order");

        try {
            // 类加载动态加载路由路径对象
            ARouter$$Path$$order path = (ARouter$$Path$$order) clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            // 获取目标对象封装
            RouterBean bean = pathMap.get("/order/Order_MainActivity");

            if (bean != null) {
                startActivity(new Intent(this, bean.getClazz()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpPersonal(View view) {
        Intent intent = new Intent(this, Personal_MainActivity.class);
        intent.putExtra("name", "simon");
        startActivity(intent);
    }
}
