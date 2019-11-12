package com.netease.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.eventbus.annotation.Subscribe;
import com.netease.eventbus.annotation.mode.ThreadMode;
import com.netease.eventbus.library.EventBus;
import com.netease.eventbus.model.UserInfo;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    // 发送事件按钮
    public void post(View view) {
        // 发送消息 / 事件
        EventBus.getDefault().post(new UserInfo("simon", 35));
        finish();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                EventBus.getDefault().post(new UserInfo("simon", 35));
//                finish();
//            }
//        }).start();
    }

    // 激活粘性按钮
    public void sticky(View view) {
        EventBus.getDefault().register(this);
        EventBus.getDefault().removeStickyEvent(UserInfo.class);
    }

    // Sticky粘性，美 [ˈstɪki]
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void sticky(UserInfo user) {
        Log.e("sticky", user.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 示例代码
        UserInfo userInfo = EventBus.getDefault().getStickyEvent(UserInfo.class);
        if (userInfo != null) {
            UserInfo info = EventBus.getDefault().removeStickyEvent(UserInfo.class);
            if (info != null) {
                EventBus.getDefault().removeAllStickyEvents();
            }
        }
        EventBus.getDefault().unregister(this);
    }
}
