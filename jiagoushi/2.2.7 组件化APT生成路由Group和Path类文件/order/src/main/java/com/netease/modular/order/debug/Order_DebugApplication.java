package com.netease.modular.order.debug;

import android.app.Application;
import android.util.Log;

import com.netease.common.utils.Cons;

public class Order_DebugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Cons.TAG, "order/debug/Order_DebugApplication");
    }
}
