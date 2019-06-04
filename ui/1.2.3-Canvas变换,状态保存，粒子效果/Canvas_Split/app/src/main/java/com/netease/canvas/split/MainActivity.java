package com.netease.canvas.split;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SplitView(this));

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic);
//        bitmap.getWidth();
//        bitmap.getHeight();
//        int pixel = bitmap.getPixel(0, 0);
    }
}
