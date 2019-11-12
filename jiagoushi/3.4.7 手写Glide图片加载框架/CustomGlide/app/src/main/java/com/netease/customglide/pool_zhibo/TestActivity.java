package com.netease.customglide.pool_zhibo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.customglide.R;
import com.netease.customglide.pool.BitmapPool;
import com.netease.customglide.pool.LruBitmapPool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class TestActivity extends AppCompatActivity implements Runnable {

    private final String TAG = TestActivity.class.getSimpleName();

    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageView = findViewById(R.id.image);
    }

    // 复用池
    BitmapPool bitmapPool = new LruBitmapPool(1024 * 1024 * 6);

    public void testAction(View view) {
        new Thread(this).start();
    }

    private final String PATH = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(PATH);
            URLConnection urlConnection = url.openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(5000);
            final int responseCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = httpURLConnection.getInputStream();

                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;*/


                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 只那图片的周围信息，内置会只获取图片的一部分而已，值获取高宽的信息 outW，outH
                BitmapFactory.decodeStream(inputStream, null, options);
                int w = options.outWidth;
                int h = options.outHeight;*/

                int w = 1920;
                int h = 1080;

                // 使用复用池，拿去图片
                BitmapFactory.Options options2 = new BitmapFactory.Options();

                Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);
                options2.inBitmap = bitmapPoolResult;
                options2.inMutable = true;
                options2.inPreferredConfig = Bitmap.Config.RGB_565;
                options2.inJustDecodeBounds = false;
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options2); // 真正的加载

                // final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                // 加入到复用池
                bitmapPool.put(bitmap);

                // 成功 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } else {
                // 失败 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestActivity.this, "加载图片失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: 关闭 inputStream.close(); e:" + e.getMessage());
                }
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }


    /**
     * ARGB
     * @param view
     */
    public void argbAction(View view) {
        BitmapFactory.Options opts =new BitmapFactory.Options();
        // opts.inPreferredConfig = Bitmap.Config.ALPHA_8;  // 设置色彩模式位ALPHA_8 八位 1个字节
        // opts.inPreferredConfig = Bitmap.Config.ARGB_4444; // 16位 2个字节，包含了 透明度，红色，绿色，蓝色
        // opts.inPreferredConfig = Bitmap.Config.RGB_565; // 16位 2个字节 包含了 红色，绿色，蓝色，没有透明度 （比较常用，除了没有透明度，还是可以的）
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888; // 32位 4个字节 包含了 透明度，红色，绿色，蓝色（比较常用，色彩全面，内存大）

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, opts);
        imageView.setImageBitmap(bitmap);
    }
}
