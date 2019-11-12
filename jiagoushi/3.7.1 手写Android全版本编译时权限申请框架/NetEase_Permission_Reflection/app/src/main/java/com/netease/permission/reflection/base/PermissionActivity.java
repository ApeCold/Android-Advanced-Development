package com.netease.permission.reflection.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.netease.permission.library.PermissionManager;
import com.netease.permission.library.dialog.AppSettingDialog;

import java.util.List;

public class PermissionActivity extends BaseActivity {

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        // 检查用户是否拒绝过某权限，并点击了“不再询问”
        if (PermissionManager.somePermissionPermanentlyDenied(this, perms)) {
            // 显示一个对话框，引导用户开启设置中的权限
            new AppSettingDialog.Builder(this)
                    // 自由发挥api
                    .setListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e("Simon", "onPermissionDenied >>> hasDeniedForever");
                        }
                    })
                    .build()
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingDialog.SETTING_CODE) { // 从设置界面回来
            Toast.makeText(this, "设置界面返回，回调监听成功！", Toast.LENGTH_SHORT).show();
            // 该干嘛干嘛……
        }
    }
}
