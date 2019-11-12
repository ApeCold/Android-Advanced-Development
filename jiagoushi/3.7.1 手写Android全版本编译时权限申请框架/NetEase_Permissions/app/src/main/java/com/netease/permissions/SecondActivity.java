package com.netease.permissions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.netease.permissions.annotations.NeedsPermission;
import com.netease.permissions.annotations.OnNeverAskAgain;
import com.netease.permissions.annotations.OnPermissionDenied;
import com.netease.permissions.annotations.OnShowRationale;
import com.netease.permissions.library.listener.PermissionRequest;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @NeedsPermission()
    void aa() {
    }

    // 提示用户为何要开启权限
    @OnShowRationale()
    void bb(final PermissionRequest request) {
    }

    // 用户选择拒绝时的提示
    @OnPermissionDenied()
    void cc() {
    }

    // 用户选择不再询问后的提示
    @OnNeverAskAgain()
    void dd() {
    }
}
