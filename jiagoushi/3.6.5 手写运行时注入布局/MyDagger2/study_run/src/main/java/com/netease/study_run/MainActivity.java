package com.netease.study_run;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.netease.study_run.annotation.ContentView;

@ContentView(R.layout.activity_main) // 5415151
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        InjectTool.inject(this);
    }
}
