package com.netease.modular.order.debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.netease.common.utils.Cons;
import com.netease.modular.order.R;

public class Order_DebugActivity extends Order_DebugBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_debug);

        Log.e(Cons.TAG, "order/debug/Order_DebugActivity");
    }
}
