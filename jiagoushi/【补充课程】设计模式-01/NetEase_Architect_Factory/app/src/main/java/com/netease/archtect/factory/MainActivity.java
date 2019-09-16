package com.netease.archtect.factory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.netease.archtect.factory.Impl.ParameterFactory;
import com.netease.archtect.factory.core.PropertiesFactory;
import com.netease.archtect.factory.factory.SampleFactory;

/**
 * 工厂模式
 * 核心：提供一个创建对象的功能，不需要关心具体实现
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 常规编码
//        Api api = new ApiImpl();
//        api.create();

        // 简单工厂：降低了模块间的耦合度
//        Api api = SampleFactory.createApi();
//        api.create();

        // 拓展：根据参数产生不同的实现
//        Api api = ParameterFactory.createApi(2);
//        if (api != null) api.create();

        // 根据配置文件产生不同的实现
        Api api = PropertiesFactory.createApi(this);
        if (api != null) api.create();
    }
}
