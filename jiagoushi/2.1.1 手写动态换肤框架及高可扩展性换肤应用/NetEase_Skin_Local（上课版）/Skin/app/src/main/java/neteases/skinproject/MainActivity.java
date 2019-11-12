package neteases.skinproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 界面无标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_netease);
        // 申请权限
        verifyStoragePermissions(this);
    }

    // 点击换肤按钮
    public void skinAction(View view) {
        Log.e("netease >>> ", "-------------start-------------");
        long start = System.currentTimeMillis();

        String skinPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "my.skin";
        try {
            SkinEngine.getInstance().updateSkin(skinPath);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis() - start;
        Log.e("netease >>> ", "换肤耗时（毫秒）：" + end);
        Log.e("netease >>> ", "-------------end---------------");
    }

    // 点击默认按钮
    public void revertDefault(View view) {
        Log.e("netease >>> ", "-------------start-------------");
        long start = System.currentTimeMillis();

        try {
            SkinEngine.getInstance().updateSkin("分身乏术分身乏术发放松放松放松");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis() - start;
        Log.e("netease >>> ", "还原耗时（毫秒）：" + end);
        Log.e("netease >>> ", "-------------end---------------");
    }

    public void startActivityThis(View view) {
        startActivity(new Intent(this, this.getClass()));
    }


    // 申请权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    public static void verifyStoragePermissions(Activity activity) {

        //检测是否有写的权限
        int permission = ActivityCompat.checkSelfPermission(activity,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
