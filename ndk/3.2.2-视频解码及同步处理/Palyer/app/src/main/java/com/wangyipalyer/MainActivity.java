package com.dongnao.wangyipalyer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    WangyiPlayer wangyiPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager
                .LayoutParams.FLAG_KEEP_SCREEN_ON);
        surfaceView = findViewById(R.id.surfaceView);
        wangyiPlayer = new WangyiPlayer();
        wangyiPlayer.setSurfaceView(surfaceView);
    }


    public void open(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "input.mp4");
        wangyiPlayer.start(file.getAbsolutePath());
    }
}
