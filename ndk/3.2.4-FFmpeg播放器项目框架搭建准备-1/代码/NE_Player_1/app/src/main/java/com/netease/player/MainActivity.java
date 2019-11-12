package com.netease.player;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private NEPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        player = new NEPlayer();
        player.setDataSource(new File(
                Environment.getExternalStorageDirectory() + File.separator + "xxxx.mp4").getAbsolutePath());
        player.setListener(new NEPlayer.MyErrorListener() {
            @Override
            public void onError(int errorCode) {
                switch (errorCode){
                    case -1:
                        //
//                        Toast
                        break;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        player.prepare();

    }
}
