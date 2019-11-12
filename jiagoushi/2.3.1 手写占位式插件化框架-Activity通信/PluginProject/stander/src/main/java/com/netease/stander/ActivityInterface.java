package com.netease.stander;

import android.app.Activity;
import android.os.Bundle;

public interface ActivityInterface {

    /**
     * 把宿主(app)的环境  给  插件
     * @param appActivity
     */
    void insertAppContext(Activity appActivity);

    // 生命周期方法
    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onDestroy();

}
