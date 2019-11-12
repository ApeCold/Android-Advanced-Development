package com.netease.permission.reflection.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.netease.ioc.library.InjectManager;
import com.netease.permission.library.PermissionManager;
import com.netease.permission.library.listener.PermissionCallback;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements PermissionCallback {

    private static final String TAG = "PermissionCallback";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionGranted:    requestCode=" + requestCode + "/perms.size()=" + perms.size());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(requestCode, permissions, grantResults, this);
    }

}
