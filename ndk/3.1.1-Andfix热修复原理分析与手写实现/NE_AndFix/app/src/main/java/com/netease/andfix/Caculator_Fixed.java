package com.netease.andfix;

import android.content.Context;
import android.widget.Toast;

public class Caculator_Fixed {
    @MethodReplace(className = "com.netease.andfix.Caculator",methodName = "caculator")
    public void caculator(Context context) {
        int a = 666;
        int b = 1;
        Toast.makeText(context, "修复好了，计算a/b = " + a / b, Toast.LENGTH_SHORT).show();
    }
}
