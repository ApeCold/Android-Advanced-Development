package com.netease.customglide;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.netease.customglide.fragment.ActivityFragmentManager;
import com.netease.customglide.fragment.FragmentActivityFragmentManager;

public class RequestManager {

    private final String TAG = RequestManager.class.getSimpleName();

    private final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_NAME";
    private final String ACTIVITY_NAME = "activity_name";
    private final int NEXT_HANDLER_MSG = 995465;

    private Context requestManagerContext;
    private static RequestTargetEngine requestTargetEngine;

    // 构造代码块，不用再所有的构造方法里面去实例化了，统一的去写
    {
        if (requestTargetEngine == null) {
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    /**
     * 可以管理生命周期 - FragmentActivity是有生命周期方法的(Fragment)
     * @param fragmentActivity
     */
    FragmentActivity fragmentActivity;
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        this.fragmentActivity = fragmentActivity;

        // 拿到Fragment
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (null == fragment) { // 如果等于null，就要去创建Fragment
            fragment = new FragmentActivityFragmentManager(requestTargetEngine); // Fragment的生命周期与requestTargetEngine关联起来了
            // 添加到 supportFragmentManager
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss(); // 提交
        }

        // 发送一次Handler
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

        Fragment fragment2 = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        Log.d(TAG, "RequestManager: fragment2" + fragment2); // null ： @3 还在排队中，还没有消费
    }

    /**
     * 可以管理生命周期 -- Activity是有生命周期方法的(Fragment)
     * @param activity
     */
    // todo @2
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;

        // TODO @3
        // 拿到Fragment
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if (null == fragment) {
            fragment = new ActivityFragmentManager(requestTargetEngine); // Fragment的生命周期与requestTargetEngine关联起来了
            // 添加到管理器 -- fragmentManager.beginTransaction().add.. Handler
            fragmentManager.beginTransaction().add(fragment, ACTIVITY_NAME).commitAllowingStateLoss(); // 提交
        }

        android.app.Fragment fragment2 = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        Log.d(TAG, "RequestManager: fragment2" + fragment2); // null ： @3 还在排队中，还没有消费

        // 发送一次Handler
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Fragment fragment2 = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            Log.d(TAG, "Handler: fragment2" + fragment2); // 有值 ： 不在排队中，所以有值
            return false;
        }
    });

    /**
     * 代表无法去管理生命周期 -- 因为Application无法管理
     * @param context
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    /**
     * load 拿到要显示的图片路径
     * @param s
     * @return
     */
    public RequestTargetEngine load(String s) {
        // 移除Handler
        mHandler.removeMessages(NEXT_HANDLER_MSG);

        return requestTargetEngine;
    }
}
