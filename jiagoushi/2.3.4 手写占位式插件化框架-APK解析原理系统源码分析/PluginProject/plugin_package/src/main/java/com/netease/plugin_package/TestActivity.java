package com.netease.plugin_package;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class TestActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
    }
}
