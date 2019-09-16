package com.netease.pmsproject.pms;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.netease.pmsproject.R;
import com.netease.pmsproject.pms.MyContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MyIPackageManager.Stub // 暂停

        // OS
        Context context = this;

        try {
            context.getPackageManager().getPackageInfo(null, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        // My
        MyContext myContext = new MyContext();
        myContext.getPackageManager().getPackageInfo(null, 0);
    }
}
