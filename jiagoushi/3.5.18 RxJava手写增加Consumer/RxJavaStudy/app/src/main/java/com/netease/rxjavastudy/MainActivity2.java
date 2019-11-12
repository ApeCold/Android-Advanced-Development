package com.netease.rxjavastudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * TODO 创建型操作符
 */
public class MainActivity2 extends AppCompatActivity {

    private final String TAG = MainActivity2.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * create 操作符 创建 Observable
     * @param view
     */
    public void r01(View view) {

        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 上游发射的
                e.onNext("A"); // 使用者自己发射
            }
        })

        // 订阅
        .subscribe(


                // 下游
                new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游接收 onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        })
        ;

    }


    /**
     * just 操作符 创建 Observable
     * @param view
     */
    public void r02(View view) {
        // 上游
        Observable.just("A", "B")  // 内部会去发射 A B

        // 订阅
        .subscribe(

                // 下游
                new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );
    }

    /**
     * fromArray 操作符 创建 Observable
     * @param view
     */
    public void r03(View view) {

        /*String[] strings = {"1", "2", "3"}; // 内部会去发射 1 2 3

        // 上游 被观察者
        Observable.fromArray(strings)

        // 订阅
        .subscribe(

                // 下游
                new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );*/

        String[] strings = {"张三", "李四", "王五"};

        // for
        for (String string : strings) {
            Log.d(TAG, "r03: " + string);
        }

        Log.d(TAG, "r03: ----- ");

        // RxJava
        Observable.fromArray(strings)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });
    }

    /**
     * 为什么只支持Object ？
     * 上游没有发射有值得事件，下游无法确定类型，默认Object，RxJava泛型 泛型默认类型==Object
     *
     * 做一个耗时操作，不需要任何数据来刷新UI， empty的使用场景之一
     *
     * @param view
     */
    public void r04(View view) {
        // 上游无法指定 事件类型
        Observable.empty() // 内部一定会只调用 发射 onComplete 完毕事件

                // 订阅
                .subscribe(

                 // 下游 观察者
                 new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object integer) {
                        // 没有事件可以接受
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");

                        // 隐藏 加载框...
                    }
                }

                        /*// 简化版 观察者
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                // 接受不到
                                // 没有事件可以接受
                                Log.d(TAG, "accept: " + o);
                            }
                        }*/

                );
    }

    public void r05(View view){

        // 上游  range内部会去发射
        // Observable.range(1, 8) // 1 2 3 4 5 6 7 8  从1开始加 数量共8个
        Observable.range(80, 5) // 80开始  80 81 82 83 84  加    数量共5个

        // 订阅
        .subscribe(

           // 下游
           new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        });

    }
}
