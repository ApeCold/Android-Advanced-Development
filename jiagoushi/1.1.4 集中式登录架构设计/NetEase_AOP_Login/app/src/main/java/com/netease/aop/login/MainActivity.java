package com.netease.aop.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.aop.login.annotation.ClickBehavior;
import com.netease.aop.login.annotation.LoginCheck;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "netease >>> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 登录点击事件（用户行为统计）
    @ClickBehavior("登录")
    public void login(View view) {
        Log.e(TAG, "模拟接口请求……验证通过，登录成功！");
    }

    // 用户行为统计（友盟统计？！后台要求自己统计）
    @ClickBehavior("我的专区")
    @LoginCheck
    public void area(View view) {
        Log.e(TAG, "开始跳转到 -> 我的专区 Activity");
        startActivity(new Intent(this, OtherActivity.class));
    }

    // 用户行为统计
    @ClickBehavior("我的优惠券")
    @LoginCheck
    public void coupon(View view) {
        Log.e(TAG, "开始跳转到 -> 我的优惠券 Activity");
        startActivity(new Intent(this, OtherActivity.class));
    }

    // 用户行为统计
    @ClickBehavior("我的积分")
    @LoginCheck
    public void score(View view) {
        Log.e(TAG, "开始跳转到 -> 我的积分 Activity");
        startActivity(new Intent(this, OtherActivity.class));
    }
}
