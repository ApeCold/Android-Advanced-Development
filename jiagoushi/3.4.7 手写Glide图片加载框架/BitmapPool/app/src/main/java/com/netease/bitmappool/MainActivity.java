package com.netease.bitmappool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.netease.bitmappool.pool.BitmapPool;
import com.netease.bitmappool.pool.BitmapPoolImpl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Runnable {

    private ImageView imageView;
    private BitmapPool bitmapPool = new BitmapPoolImpl(1024 * 1024 * 6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
    }

    private final String PATH = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";

    public void testAction(View view) {
        new Thread(this).start();
    }

    // 应用层 Http Https  ---> HttpURLConnection
    // 应用层 OkHttp 高效
    @Override
    public void run() {
        try {
            URL url = new URL(PATH);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                InputStream inputStream = connection.getInputStream();

                /*BitmapFactory.Options options = new BitmapFactory.Options(); // 拿到图片宽和高
                options.inJustDecodeBounds = true; // 只拿到周围信息，outXXX， outW，outH
                // options = null; 执行下面代码
                BitmapFactory.decodeStream(inputStream, null, options);
                int w = options.outWidth;
                int h = options.outHeight;*/

                int w = 1920;
                int h = 1080;

                BitmapFactory.Options options = new BitmapFactory.Options();

                // 拿到复用池  条件： bitmap.isMutable() == true;
                Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);

                // 如果设置为null，内部就不会去申请新的内存空间，无非复用，依然会照成：内存抖动，内存碎片
                options.inBitmap = bitmapPoolResult; // 把复用池的Bitmap 给 inBitmap
                options.inPreferredConfig = Bitmap.Config.RGB_565; // 2个字节
                options.inJustDecodeBounds = false;
                options.inMutable = true; // 符合 复用机制
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options); // 复用内存

                // 添加到复用池
                bitmapPool.put(bitmap);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }

        } catch (Exception e) {
        }
    }
}
