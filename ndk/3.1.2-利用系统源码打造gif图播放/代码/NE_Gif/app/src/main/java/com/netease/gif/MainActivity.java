package com.netease.gif;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView image;
    private GifNDKDecoder gifNDKDecoder;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.image);

    }


    public void ndkLoadGif(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "demo2.gif");
        gifNDKDecoder = GifNDKDecoder.load(file.getAbsolutePath());
        int width = GifNDKDecoder.getWidth(gifNDKDecoder.getGifPointer());
        int height = GifNDKDecoder.getHeight(gifNDKDecoder.getGifPointer());
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int nextDelayTime = gifNDKDecoder.updateFrame(bitmap, gifNDKDecoder.getGifPointer());
        mHandler.sendEmptyMessageDelayed(1, nextDelayTime);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int nextDelayTime = gifNDKDecoder.updateFrame(bitmap, gifNDKDecoder.getGifPointer());
            mHandler.sendEmptyMessageDelayed(1, nextDelayTime);
            image.setImageBitmap(bitmap);
        }
    };
}
