package com.netease.hookproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
    }
}
