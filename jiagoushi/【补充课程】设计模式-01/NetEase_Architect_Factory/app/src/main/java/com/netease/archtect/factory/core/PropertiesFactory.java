package com.netease.archtect.factory.core;

import android.content.Context;

import com.netease.archtect.factory.Api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFactory {

    public static Api createApi(Context context) {
        try {
            // 加载配置文件
            Properties props = new Properties();
            // 如果放入了 app/src/main/assets文件中
            InputStream inputStream = context.getAssets().open("config.properties");

            // 如果放入了 app/src/main/res/raw 文件中
            // InputStream inputStream = context.getResources().openRawResource("config.properties");

            // Java的写法
            // InputStream inputStream = PropertiesFactory.class.getResourceAsStream("assets/config.properties");
            props.load(inputStream);

            Class clazz = Class.forName(props.getProperty("create_b"));
            return (Api) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
