package com.netease.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * TODO 上游和下游
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // todo 这是第一节课 RxJava概念的代码
    public void r01(View view) {
        // 起点 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        })


        .subscribe( // 订阅 == registerObserver



                // 终点 一个 观察者
                new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    /**
     * 拆分来写
     * @param view
     */
    public void r02(View view) {

        // TODO 上游 Observable 被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            // ObservableEmitter<Integer> emitter 发射器 发射事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "上游subscribe: 发射事件");
                // 发射事件
                emitter.onNext(1);
                Log.d(TAG, "上游subscribe: 发射完成");
            }
        });


        // TODO 下游 Observer 观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游 接收处理 onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        // TODO 被观察者(上游)  订阅  观察者（下游）
        observable.subscribe(observer);
    }

    /**
     * 链式调用
     * @param view
     */
    public void r03(View view){

        // TODO 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {

            // ObservableEmitter<Integer> emitter 发射器 发射事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        })
         // Observable.subscribe
         // 订阅操作
         .subscribe(
                 // TODO 下游 Observer 观察者
                 new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    /**
     * 流程整理 1
     * @param vieww
     */
    public void r04(View vieww) {

        // 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                // 发射
                Log.d(TAG, "上游 subscribe: 开始发射..."); // todo 2
                emitter.onNext("RxJavaStudy");

                emitter.onComplete(); // 发射完成  // todo 4

                // 上游的最后log才会打印
                Log.d(TAG, "上游 subscribe: 发射完成");
            }
        }).subscribe(
         // 下游 Observer 观察者
         new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                // 弹出 加载框 ....
                Log.d(TAG, "上游和下游订阅成功 onSubscribe 1"); // todo 1
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游接收 onNext: " + s); // todo 3
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                // 隐藏加载框
                Log.d(TAG, "下游接收完成 onComplete"); // todo 5  只有接收完成之后，上游的最后log才会打印
            }
        });

        /**
         *  D/MainActivity: 上游和下游订阅成功 onSubscribe 1
         *  D/MainActivity: 上游 subscribe: 开始发射...
         *  D/MainActivity: 下游接收 onNext: RxJavaStudy
         *  D/MainActivity: 下游接收完成 onComplete
         *  D/MainActivity: 上游 subscribe: 发射完成
         */
    }

    /**
     * 流程整理2
     * @param vieww
     */
    public void r05(View vieww) {

        // 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                // 发射
                Log.d(TAG, "上游 subscribe: 开始发射..."); // todo 2
                emitter.onNext("RxJavaStudy");

                // emitter.onComplete(); // 发射完成  // todo 4

                // 上游的最后log才会打印
                // Log.d(TAG, "上游 subscribe: 发射完成");

                // emitter.onError(new IllegalAccessException("error rxJava"));

                // TODO 结论：在 onComplete();/onError 发射完成 之后 再发射事件  下游不再接收上游的事件
                /*emitter.onNext("a");
                emitter.onNext("b");
                emitter.onNext("c");*/
                // 发一百个事件

                emitter.onError(new IllegalAccessException("error rxJava")); // 发射错误事件
                emitter.onComplete(); // 发射完成
                // TODO 结论：已经发射了onComplete();， 再发射onError RxJava会报错，不允许
                // TODO 结论：先发射onError，再onComplete();，不会报错， 有问题（onComplete不会接收到了）
            }
        }).subscribe(
                // 下游 Observer 观察者
                new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // 弹出 加载框 ....
                        Log.d(TAG, "上游和下游订阅成功 onSubscribe 1"); // todo 1
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "下游接收 onNext: " + s); // todo 3
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "下游接收 onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        // 隐藏加载框
                        Log.d(TAG, "下游接收完成 onComplete"); // todo 5  只有接收完成之后，上游的最后log才会打印
                    }
                });

    }

    Disposable d;

    /**
     * 切断下游，让下游不再接收上游的事件，也就是说不会去更新UI
     * @param view
     */
    public void r06(View view) {
        // TODO 上游 Observable
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        })

        // 订阅下游
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                MainActivity.this.d = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游接收 onNext: " + integer);

                // 接收上游的一个事件之后，就切断下游，让下游不再接收
                // d.dispose();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 切断下游
        if (d != null) d.dispose();
    }
}
