package com.netease.rxjavastudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO 给同学们增加的，修正一些代码，同学们可以看看
 * TODO 异常处理操作符，在RxJava使用的时候 的异常处理，提供操作符
 */
public class MainActivity11 extends AppCompatActivity {

    private final String TAG = MainActivity11.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 异常级别
     *      Error  严重错误
     *      Exception  还可以拯救  还可以处理好
     *
     *  onErrorReturn
     *     onError(Error  Exception）
     *
     * todo 通用点 onErrorReturn异常操作符：能够接收e.onError(Error/Exception) / throw new xxxException，
     * todo  [onErrorReturn] 此异常操作符特点：能够打印异常详情，会给下游一个标记
     * todo 无法处理throw new xxxError, 错误  ---》 奔溃
     * @param view
     */
    public void r01(View view) {

        // 上游 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的
                        // throw new IllegalAccessException("我要报错了"); // 抛出异常，可以接收

                        // RxJava标准的
                        // e.onError(new IllegalAccessError("我要报错了")); // 发射此事件，可以接收
                        // e.onError(new IllegalAccessException("我要报废了")); // 发射此事件，可以接收

                        // 错误
                        throw new IllegalAccessError("我要报错了"); // 一定会奔溃
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        })

        // 在上游 和 下游之间 添加异常操作符
        .onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                // 处理，纪录，异常，通知给下一层
                Log.d(TAG, "onErrorReturn: " + throwable.getMessage());
                return 400; // 400代表有错误，给下一层，目前 下游 观察者    -----> 下游 onNext方法
            }
        })

        .subscribe(new Observer<Integer>() { // 完整版 下游 观察者
            @Override
            public void onSubscribe(Disposable d) {

            }

            // 如果使用了 异常操作符 onNext: 400
            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer); // 400
            }

            // 如果不使用 异常操作符 onError
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });

    }



    // onErrorResumeNext 异常操作符：能够接收e.onError(xxx)/throw new xxxException， 无法处理throw new xxxError
    // onErrorReturn可以返回标识400    对比   onErrorResumeNext可以返回被观察者（被观察者可以再次发射多次事件给 下游）
    // 特点：不能打印异常详情，可以多少发射事件
    /**
     * 异常级别
     *      Error  严重错误
     *      Exception  还可以拯救  还可以处理好
     *
     *  onErrorResumeNext
     *     onError(Error  Exception）
     *
     * todo 通用点 onErrorReturn异常操作符：能够接收e.onError(Error/Exception) / throw new xxxException，
     * todo  [onErrorResumeNext] 此异常操作符特点：onErrorResumeNext可以返回被观察者（被观察者可以再次发射多次事件给 下游）
     * todo 无法处理throw new xxxError, 错误  ---》 奔溃
     * @param view
     */
    public void r02(View view) {

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的
                        // throw new IllegalAccessException("我要报错了");

                        // RxJava标准的
                        e.onError(new IllegalAccessError("我要报错了"));  // 发射此事件，能够接收
                        // e.onError(new IllegalAccessError("我要报错了"));  // 发射此事件，能够接收

                        // 程序奔溃
                        // throw new IllegalAccessError("我要报错了"); // 一定会奔溃
                    } else {
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        })

        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {

                // onErrorResumeNext 返回的是 被观察者，所以再多次发射给 下游 给 观察者接收
                return Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onComplete();
                    }
                });
            }
        })

        .subscribe(new Observer<Integer>() { // 下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        })    ;

    }


    /**
     * 异常级别
     *      Error  严重错误
     *      Exception  还可以拯救  还可以处理好
     *
     *  onExceptionResumeNext
     *      onError(Exception）
     *
     * todo 通用点 onExceptionResumeNext异常操作符：只能接收e.onError(Exception) / throw new xxxException，
     * todo  [onExceptionResumeNext] 此异常操作符特点： 专门处理Exception，不能处理Error
     * todo 无法处理throw new xxxError, 错误  ---》 奔溃
     * @param view
     */
    public void r03(View view) {

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的
                        // throw new IllegalAccessException("我要报错了");

                        // RxJava标准的

                        // 异常操作符 无法无法处理  直接就到了  下游的onError了
                        e.onError(new IllegalAccessError("我要报错了")); // 发射此事件，无法处理到此操作，直接到下游的onError了

                        // e.onError(new IllegalAccessException("我要报错了")); // 发射此事件，可以处理到此操作，会onNext(404) -->

                        // throw new IllegalAccessError("我要报错了"); // 一定会奔溃
                    } else {
                        e.onNext(i);
                    }

                }
                e.onComplete(); // 一定要最后执行
            }
        })

        // 在上游和下游中间 增加 异常操作符
        .onExceptionResumeNext(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                observer.onNext(404); // 可以让程序 不崩溃的
                observer.onNext(4);
                observer.onNext(4);
                observer.onNext(4);
                observer.onNext(4);
                observer.onNext(4);
                observer.onNext(4);
                observer.onNext(4);
                observer.onNext(4);
                // ...
            }
        })

        // 下游
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    /**
     * retry 重试操作符 异常处理操作符中
     * @param view
     */
    public void r04(View view) {

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                       /* throw new 其他Exception("错了");
                        throw new IllegalAccessException("错了");*/
                        // throw new Exception("错了");

                        // rxJava标准的
                        e.onError(new IllegalAccessException("错了")); // 异常事件
                    } else {
                        e.onNext(i);
                    }
                }
                e.onComplete(); // 一定要最后执行
            }
        })

        // todo 演示一
        /*.retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                Log.d(TAG, "retry: " + throwable.getMessage());
                // return false; // 代表不去重试
                return true; // 一直重试，不停的重试
            }
        })*/

        // todo 演示二 重试次数
        /*.retry(3, new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                Log.d(TAG, "retry: " + throwable.getMessage());
                return true;
            }
        })*/

        // todo 演示三 打印重试了多少次，计数     Throwable  +  count
        .retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer integer, Throwable throwable) throws Exception {
                Thread.sleep(2);
                Log.d(TAG, "retry: 已经重试了:" + integer + "次  e：" + throwable.getMessage());
                return true; // 重试
            }
        })

        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });

    }

    // TODO 作业   手写 flatMap  -- 学会了 泛型进阶
    private void testStudy() {
        Observable.just("")
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        return null;
                    }
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    
 }
