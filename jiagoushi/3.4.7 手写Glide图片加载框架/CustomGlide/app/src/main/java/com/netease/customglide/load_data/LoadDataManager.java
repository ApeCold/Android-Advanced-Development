package com.netease.customglide.load_data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.netease.customglide.Tool;
import com.netease.customglide.resource.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoadDataManager implements ILoadData, Runnable {

    private final String TAG = LoadDataManager.class.getSimpleName();

    private String path;
    private ResponseListener responseListener;
    private Context context;

    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;

        // 加载 网络图片/SD本地图片/..

        Uri uri = Uri.parse(path);

        // 网络图片
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
                new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                        60, TimeUnit.SECONDS,
                        new SynchronousQueue<Runnable>()).execute(this);
        }

        // SD本地图片 返回Value

        // ....

        return null;
    }


    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(path);
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

                // 不需要使用复用池，拿去图片内存
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                //   既然是外部网络加载图片，就不需要用复用池 Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);
                //   options2.inBitmap = bitmapPoolResult; // 如果我们这里拿到的是null，就不复用
                options2.inMutable = true;
                options2.inPreferredConfig = Bitmap.Config.RGB_565;
                options2.inJustDecodeBounds = false;
                // inSampleSize:是采样率，当inSampleSize为2时，一个2000 1000的图片，将被缩小为1000 500， 采样率为1 代表和原图宽高最接近
                options2.inSampleSize = Tool.sampleBitmapSize(options2, w, h);
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options2); // 真正的加载

                // final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                // 成功 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setmBitmap(bitmap);

                        // 回调成功
                        responseListener.responseSuccess(value);
                    }
                });
            } else {
                // 失败 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        responseListener.responseException(new IllegalStateException("请求失败 请求码:" + responseCode));
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
}
