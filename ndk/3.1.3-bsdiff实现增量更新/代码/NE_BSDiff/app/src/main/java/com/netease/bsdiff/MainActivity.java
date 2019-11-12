package com.netease.bsdiff;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv_version);
        tv.setText("当前版本：" + BuildConfig.VERSION_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES};
            if (checkSelfPermission(
                    perms[0]) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }

    public void onUpdate(View view) {
        //网络请求下载查分包（省略。直接拷贝查分包到sd卡）

        new AsyncTask<Void, Void, File>() {

            @Override
            protected File doInBackground(Void... voids) {
                //bspatch 做合成 得到新版本的apk文件
                //sz: linux>windows
                //rz: windows>linux
                String patch = new File(Environment.getExternalStorageDirectory(),
                        "patch.diff").getAbsolutePath();
                File newApk = new File(Environment.getExternalStorageDirectory(), "new.apk");
                if (!newApk.exists()) {
                    try {
                        newApk.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String oldApk = getApplicationInfo().sourceDir;
                doPatchNative(oldApk, newApk.getAbsolutePath(), patch);
                return newApk;
            }

            @Override
            protected void onPostExecute(File apkFile) {
                //安装
                if (!apkFile.exists()) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 24) { //Android 7.0及以上
                    // 参数2 清单文件中provider节点里面的authorities ; 参数3  共享的文件,即apk包的file类
                    Uri apkUri = FileProvider.getUriForFile(MainActivity.this,
                            getApplicationInfo().packageName + ".provider", apkFile);
                    //对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile),
                            "application/vnd.android.package-archive");
                }
                startActivity(intent);
            }
        }.execute();

    }

    private native void doPatchNative(String oldApk, String newApk, String patch);

}
