package com.dongnao.wangyimusic;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


//MP3 解码  pcm  ---》文件  原始
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void play(View view) {
        WangyiPlayer wangyiPlayer = new WangyiPlayer();
        String input = new File(Environment.getExternalStorageDirectory(),"input.mp3").getAbsolutePath();
        String output = new File(Environment.getExternalStorageDirectory(),"output.pcm").getAbsolutePath();
        wangyiPlayer.sound(input, output);
    }
}
