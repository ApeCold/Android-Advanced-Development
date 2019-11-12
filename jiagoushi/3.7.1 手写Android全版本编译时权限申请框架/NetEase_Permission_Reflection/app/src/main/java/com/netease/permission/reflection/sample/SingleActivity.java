package com.netease.permission.reflection.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.netease.permission.reflection.R;

import java.util.ArrayList;
import java.util.List;

public class SingleActivity extends AppCompatActivity {

    private final static int REQUEST_CODE = 3; // 回调码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRuntimePermission(new String[]{Manifest.permission.CAMERA});
            }
        });

        findViewById(R.id.button_location_and_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRuntimePermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CONTACTS});
            }
        });
    }

    // 发起权限申请
    private void requestRuntimePermission(String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 遍历权限组参数，将没有权限的加入集合
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
            }
        }

        // 不为空发起权限申请
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE);
        } else {
            Toast.makeText(this, "权限全部通过", Toast.LENGTH_SHORT).show();
            // 省略业务、逻辑一万行代码……
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                // 如果有权限被拒绝
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            // 表明用户没有彻底禁止弹出权限请求
                            if (isTip) {
                                deniedPermissions.add(permission);
                            } else { // 表明用户已经彻底禁止弹出权限请求
                                // 新建对话框
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("");
                                builder.setMessage("");
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent()); // 省略代码，跳转设置
                                    }
                                });
                                builder.create();
                                builder.show();
                            }
                        }
                    }

                    // 如果全部授权成功
                    if (deniedPermissions.isEmpty()) {
                        Toast.makeText(this, "权限全部通过", Toast.LENGTH_SHORT).show();
                        // 省略业务、逻辑一万行代码……
                    } else {
                        ActivityCompat.requestPermissions(this,
                                deniedPermissions.toArray(new String[deniedPermissions.size()]), REQUEST_CODE);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果从设置界面回来，requestCode匹配，再次判断权限是否通过
        // 省略业务、逻辑一万行代码……
    }

}
