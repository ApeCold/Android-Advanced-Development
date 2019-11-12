package com.netease.andfix;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
//    Caculator caculator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//         caculator = new Caculator();
    }

    public void caculator(View view) {
        new Caculator().caculator(this);
//        caculator.caculator(this);
    }

    public void fix(View view) {
        DexManager.getInstance().setContext(this);
        DexManager.getInstance().loadDex(new File(Environment.getExternalStorageDirectory(),"out.dex"));
    }
}
