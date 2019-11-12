package com.netease.rxjavastudy;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * todo  RxJava线程切换的学习
 */
public class MainActivity8 extends AppCompatActivity {

    private final String TAG = MainActivity8.class.getSimpleName();

    private ImageView imageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imageView =  findViewById(R.id.image);

        Log.d(TAG, "onCreate: " + Thread.currentThread().getName());  // 主线程main  安卓主线程
    }

    /**
     * todo 异步线程区域
     * Schedulers.io() ：代表io流操作，网络操作，文件流，耗时操作
     * Schedulers.newThread()    ： 比较常规的，普普通通
     * Schedulers.computation()  ： 代表CPU 大量计算 所需要的线程
     *
     * todo main线程 主线程
     * AndroidSchedulers.mainThread()  ： 专门为Android main线程量身定做的
     *
     * todo 给上游分配多次，只会在第一次切换，后面的不切换了（忽略）
     * todo 给下游分配多次，每次都会去切换
     *
     * @param view
     */
    public void r01(View view) {
        // RxJava如果不配置，默认就是主线程main
        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "上游 subscribe: " + Thread.currentThread().getName());

                e.onNext("");
            }
        }).subscribeOn(Schedulers.io()) // todo 给上游配置异步线程    // 给上游分配多次，只会在第一次切换，后面的不切换了
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                .subscribeOn(AndroidSchedulers.mainThread()) // 被忽略
                // result: io 异步线程

          .observeOn(AndroidSchedulers.mainThread()) // todo 给下游配置 安卓主线程    // 给下游分配多次，每次都会去切换
                .observeOn(AndroidSchedulers.mainThread()) // 切换一次线程
                .observeOn(AndroidSchedulers.mainThread()) // 切换一次线程
                .observeOn(AndroidSchedulers.mainThread()) // 切换一次线程
                .observeOn(Schedulers.io()) // 切换一次线程
                // result: io 异步线程

                .subscribe(new Consumer<String>() { // 下游简化版
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "下游 subscribe: " + Thread.currentThread().getName());
            }
        });
    }

    /**
     * todo 同步 和 异步 执行流程
     * @param view
     */
    public void r02(View view) {

        // TODO 默认情况下  上游和下游都是main线程的情况下
        // 上游
        /*Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: 上游发送了一次 1 ");
                e.onNext(1);

                Log.d(TAG, "subscribe: 上游发送了一次 2 ");
                e.onNext(2);

                Log.d(TAG, "subscribe: 上游发送了一次 3 ");
                e.onNext(3);
            }
        }).subscribe(new Consumer<Integer>() { // 下游简化版
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "下游 accept: " + integer);
            }
        });*/

        /**
         * 默认情况下 同步的想象
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 1
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 1
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 2
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 2
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 3
         * 09-05 16:23:46.064 8088-8088/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 3
         */






        // TODO 配置好异步线程
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: 上游发送了一次 1 ");
                e.onNext(1);

                Log.d(TAG, "subscribe: 上游发送了一次 2 ");
                e.onNext(2);

                Log.d(TAG, "subscribe: 上游发送了一次 3 ");
                e.onNext(3);
            }
        }).subscribeOn(Schedulers.io()) // 给上游分配 异步线程
          .observeOn(AndroidSchedulers.mainThread()) // 给下游分配 主线程

          .subscribe(new Consumer<Integer>() { // 下游简化版
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "下游 accept: " + integer);
            }
        });
        /**
         * 配置了 异步后
         * 09-05 16:26:03.574 9547-9690/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 1
         * 09-05 16:26:03.574 9547-9690/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 2
         * 09-05 16:26:03.574 9547-9690/com.netease.rxjavastudy D/MainActivity8: subscribe: 上游发送了一次 3
         * 09-05 16:26:03.574 9547-9547/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 1
         * 09-05 16:26:03.574 9547-9547/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 2
         * 09-05 16:26:03.575 9547-9547/com.netease.rxjavastudy D/MainActivity8: 下游 accept: 3
         */
    }

    private final String PATH = "http://pic33.nipic.com/20131007/13639685_123501617185_2.jpg";

    /**
     * todo 不使用RxJava去下载图片
     * @param view
     */
    public void r03(View view) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在下载中...");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);

                    URL url = new URL(PATH);
                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.setConnectTimeout(5000);
                    int responseCode = httpURLConnection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == responseCode) {
                       Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                       Message message = handler.obtainMessage();
                       message.obj = bitmap;
                       handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            imageView.setImageBitmap(bitmap);

            // 隐藏加载框
            if (progressDialog != null)
                progressDialog.dismiss();
            return false;
        }
    });


    // 回调  接口处理


    /**
     * todo 使用RxJava去下载图片
     * @param view
     */
    public void r04(View view) {

        // 起点

        // 上游 被观察者 Observable
        Observable.just(PATH)  // 内部发射

        // String Path  变换  Bitmap
        .map(new Function<String, Bitmap>() {
            @Override
            public Bitmap apply(String s) throws Exception {

                try {
                    Thread.sleep(2000);

                    URL url = new URL(PATH);
                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.setConnectTimeout(5000);
                    int responseCode = httpURLConnection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == responseCode) {
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        return bitmap;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        })

        .map(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) throws Exception {
                // 给图片加水印
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setTextSize(30);
                Bitmap bitmapSuccess = drawTextToBitmap(bitmap, "同学们大家好", paint, 60, 60);
                return bitmapSuccess;
            }
        })

        // 比如：增加一个 日志纪录功能，只需要添加要给 变换操作符
        .map(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) throws Exception {
                Log.d(TAG, "apply: 下载的Bitmap 是这个样子的" + bitmap);
                return bitmap;
            }
        })

        .subscribeOn(Schedulers.io()) // todo  给上游分配 异步线程
        .observeOn(AndroidSchedulers.mainThread()) // todo  给下游分配 主线程

        .subscribe(new Observer<Bitmap>() { // 下游
            @Override
            public void onSubscribe(Disposable d) {
                progressDialog = new ProgressDialog(MainActivity8.this);
                progressDialog.setMessage("RxJava下载图片中..");
                progressDialog.show();
            }

            @Override
            public void onNext(Bitmap bitmap) {
                if (imageView != null)
                    imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onError(Throwable e) { // 发生异常
               //  if (imageView != null)
                   //  imageView.setImageResource(R.mipmap.ic_launcher); // 下载错误的图片
            }

            @Override
            public void onComplete() { // 终点
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }


    //图片上绘制文字
    private Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }
}
