package com.netease.modular.personal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.common.base.BaseActivity;
import com.netease.common.utils.Cons;

public class Personal_MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);

        Log.e(Cons.TAG, "personal/Personal_MainActivity");

        if (getIntent() != null) {
            String content = getIntent().getStringExtra("name");
            Log.e(Cons.TAG, "接收参数值：" + content);
        }
    }

    public void jumpApp(View view) {

    }

    public void jumpOrder(View view) {

    }
}
