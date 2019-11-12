package com.netease.rxjavastudy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * 练习 Flowable
 */
public class MainActivity10 extends AppCompatActivity {

    private final String TAG = MainActivity10.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void r01(View view) {

        String[] strings = {"1", "2", "3"};

        // for
        for (String string : strings) {
            Log.d(TAG, "r01: " + string);
        }

        // Observable
        // 上游
        Observable.fromArray(strings)
                .subscribe(new Consumer<String>() { // 下游 简化版
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });

        // 上游
        Flowable.fromArray(strings)
                .subscribe(new Consumer<String>() {  // 下游 简化版
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });
    }

    public void r02(View view) {

        // 上游   todo  Observable -- Observer
        // Observable
        Observable.just("李四","张三", "王五")
                .subscribe(new Observer<String>() { // 下游 Observer 完整版
                    @Override
                    public void onSubscribe(Disposable d) {
                        // 切断下游，注意：切断的是下游的水管，上游还会继续发射，只是下游无法接收了
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });




        // 上游
        // Flowable
        Flowable.just("李四","张三", "王五")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        // s.request(1); // 取出来给下游接收
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 上游 还正在执行耗时操作， Activity被关闭  onDestroy  切断下游 不去接收 d.dispose();
    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        d.dispose();
    }*/


    public void  r03(View view) {

        // 上游
        Observable.just("url")
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return null;
                    }
                })
                .flatMap(new Function<Bitmap, ObservableSource<Bitmap>>() {
                    @Override
                    public ObservableSource<Bitmap> apply(Bitmap bitmap) throws Exception {
                        // Bitmap 是伪代码
                        Bitmap bitmap1 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                        Bitmap bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                        Bitmap bitmap3 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                        return Observable.just(bitmap1, bitmap2, bitmap3);
                    }
                })
                .subscribe(new Observer<Bitmap>() { // 下游
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        // 上游
        Flowable.just("url")
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return null;
                    }
                })
                .flatMap(new Function<Bitmap, Publisher<Bitmap>>() {
                    @Override
                    public Publisher<Bitmap> apply(Bitmap bitmap) throws Exception {
                        // Bitmap 是伪代码
                        Bitmap bitmap1 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                        Bitmap bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                        Bitmap bitmap3 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                        return Flowable.just(bitmap1, bitmap2, bitmap3);
                    }
                })
                .subscribe(new Subscriber<Bitmap>() { // 下游
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    public void r04(View view) {


        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("test");
                e.onComplete();
            }
        })
        .subscribe(new Consumer<String>() { // 简化版 下游
            @Override
            public void accept(String s) throws Exception {

            }
        })     ;


        // 上游
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("test");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception { // 简化版 下游

            }
        });

    }
}
