package com.netease.skin.dynamic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.netease.skin.library.base.SkinActivity;
import com.netease.skin.library.utils.PreferencesUtils;

import java.io.File;

/**
 * 如果图标有固定的尺寸，不需要更改，那么drawable更加适合
 * 如果需要变大变小变大变小的，有动画的，放在mipmap中能有更高的质量
 */
public class MainActivity extends SkinActivity {

    private String skinPath;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // File.separator含义：拼接 /
        skinPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "net163.skin";

        // 运行时权限申请（6.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }

        if (("net163").equals(PreferencesUtils.getString(this, "currentSkin"))) {
            skinDynamic(skinPath, R.color.skin_item_color);
        } else {
            defaultSkin(R.color.colorPrimary);
        }
    }

    // 换肤按钮（api限制：5.0版本）
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void skinDynamic(View view) {
        // 真实项目中：需要先判断当前皮肤，避免重复操作！
        if (!("net163").equals(PreferencesUtils.getString(this, "currentSkin"))) {
            Log.e("netease >>> ", "-------------start-------------");
            long start = System.currentTimeMillis();

            skinDynamic(skinPath, R.color.skin_item_color);
            PreferencesUtils.putString(this, "currentSkin", "net163");

            long end = System.currentTimeMillis() - start;
            Log.e("netease >>> ", "换肤耗时（毫秒）：" + end);
            Log.e("netease >>> ", "-------------end---------------");
        }
    }

    // 默认按钮（api限制：5.0版本）
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void skinDefault(View view) {
        if (!("default").equals(PreferencesUtils.getString(this, "currentSkin"))) {
            Log.e("netease >>> ", "-------------start-------------");
            long start = System.currentTimeMillis();

            defaultSkin(R.color.colorPrimary);
            PreferencesUtils.putString(this, "currentSkin", "default");

            long end = System.currentTimeMillis() - start;
            Log.e("netease >>> ", "还原耗时（毫秒）：" + end);
            Log.e("netease >>> ", "-------------end---------------");
        }
    }

    // 跳转按钮
    public void jumpSelf(View view) {
        startActivity(new Intent(this, this.getClass()));
    }

    @Override
    protected boolean openChangeSkin() {
        return true;
    }
}
