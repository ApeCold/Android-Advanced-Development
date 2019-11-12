package com.netease.permission.reflection;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.netease.ioc.library.annotations.ContentView;
import com.netease.ioc.library.annotations.OnClick;
import com.netease.permission.library.PermissionManager;
import com.netease.permission.library.annotation.IPermission;
import com.netease.permission.reflection.base.PermissionActivity;

@ContentView(R.layout.activity_main)
public class MainActivity extends PermissionActivity {

    private static final int CAMERA_REQUEST_CODE = 111; // 拍照权限请求标识码
    private static final int LOCATION_CONTACTS_CODE = 222; // 位置、联系人权限请求标识码

    @OnClick(R.id.singlePermission)
    public void singlePermission(View btn) {
        cameraTask();
    }

    @OnClick(R.id.multiPermission)
    public void multiPermission(View btn) {
        locationContactsTask();
    }

    @IPermission(CAMERA_REQUEST_CODE)
    private void cameraTask() { // private
        if (PermissionManager.hasPermissions(this, Manifest.permission.CAMERA)) { // 授权通过
            Toast.makeText(this, "授权通过！", Toast.LENGTH_SHORT).show();
        } else { // 请求授权
            PermissionManager.requestPermissions(this, CAMERA_REQUEST_CODE, Manifest.permission.CAMERA);
        }
    }

    @IPermission(LOCATION_CONTACTS_CODE)
    private void locationContactsTask() { // private
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};
        if (PermissionManager.hasPermissions(this, perms)) { // 授权通过
            Toast.makeText(this, "授权通过！", Toast.LENGTH_SHORT).show();
        } else { // 请求授权
            PermissionManager.requestPermissions(this, LOCATION_CONTACTS_CODE, perms);
        }
    }

}
