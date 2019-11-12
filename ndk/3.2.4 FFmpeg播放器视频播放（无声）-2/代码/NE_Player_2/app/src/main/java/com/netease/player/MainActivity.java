package com.netease.player;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private NEPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        player = new NEPlayer();
        player.setDataSource(new File(
                Environment.getExternalStorageDirectory() + File.separator + "demo.mp4").getAbsolutePath());
        player.setOnpreparedListener(new NEPlayer.OnpreparedListener() {
            @Override
            public void onPrepared() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "开始播放");
                        Toast.makeText(MainActivity.this, "开始播放！", Toast.LENGTH_SHORT).show();
                    }
                });
                //播放 调用到native去
                //start play
                player.start();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        player.prepare();

    }
}
