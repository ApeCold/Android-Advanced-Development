package com.netease.permission.reflection.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.netease.ioc.library.annotations.ContentView;
import com.netease.ioc.library.annotations.OnClick;
import com.netease.permission.reflection.R;

import java.util.List;

@ContentView(R.layout.activity_more)
public class MyActivity extends MyBaseActivity implements PermissionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListener(this);
    }

    @OnClick(R.id.camera)
    public void openCamera(View btn) {
        requestRuntimePermission(new String[] {Manifest.permission.CAMERA});
    }

    @OnClick(R.id.write)
    public void saveFiles(View btn) {
        requestRuntimePermission(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @OnClick(R.id.more)
    public void more(View btn) {
        requestRuntimePermission(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onGranted() {
        Toast.makeText(this, "用户允许了权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDenied(List<String> deniedPermission) {
        Toast.makeText(this, "用户拒绝了权限", Toast.LENGTH_SHORT).show();
        // 用户点击“不在询问”
    }
}
