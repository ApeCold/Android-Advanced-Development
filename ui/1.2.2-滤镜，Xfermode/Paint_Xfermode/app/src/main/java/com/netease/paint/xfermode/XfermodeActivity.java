package com.netease.paint.xfermode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class XfermodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new XfermodeEraserView(this));
    }
}
