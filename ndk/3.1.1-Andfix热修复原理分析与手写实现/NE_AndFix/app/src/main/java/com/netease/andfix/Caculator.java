package com.netease.andfix;

import android.content.Context;
import android.widget.Toast;

public class Caculator {
    public void caculator(Context context) {
        int a = 666;
        int b = 0;
        Toast.makeText(context, "计算a/b = " + a / b, Toast.LENGTH_SHORT).show();
    }
}
