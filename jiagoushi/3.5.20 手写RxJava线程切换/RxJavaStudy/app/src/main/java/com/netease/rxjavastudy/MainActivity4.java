package com.netease.rxjavastudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * TODO 过滤操作符
 */
public class MainActivity4 extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * filter 过滤
     * 需求：过滤掉 哪些不合格的奶粉，输出哪些合格的奶粉
     * @param view
     */
    public void r01(View view) {

        // 上游
        Observable.just("三鹿", "合生元", "飞鹤")

        .filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                // return true; // 不去过滤，默认全部都会打印
                // return false; // 如果是false 就全部都不会打印

                if ("三鹿".equals(s)) {
                    return false; // 不合格
                }

                return true;
            }
        })

        // 订阅
        .subscribe(new Consumer<String>() { // 下游
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: " + s);
            }
        });
    }

    /**
     * take过滤操作符
     * @param view
     */
    public void r02(View view) {
        // 定时器 运行   只有再定时器运行基础上 加入take过滤操作符，才有take过滤操作符的价值

        // 上游
        Observable.interval(2, TimeUnit.SECONDS)

                // 增加过滤操作符，停止定时器
                .take(8) // 执行次数达到8 停止下来

                .subscribe(new Consumer<Long>() { // 下游
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "accept: " + aLong);
                    }
                });

    }

    /**
     * distinct过滤重复事件
     * @param view
     */
    public void r03(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(4);
                e.onComplete();
            }
        })

        .distinct() // 过滤重复 发射的事件

        .subscribe(new Consumer<Integer>() { // 下游 观察者
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer); // 事件不重复
            }
        });
    }

    /**
     * elementAl 指定过滤的内容
     * @param view
     */
    public void r04(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("九阴真经");
                e.onNext("九阳真经");
                e.onNext("易筋经");
                e.onNext("神照经");
                e.onComplete();
            }
        })

        // 过滤操作符
        .elementAt(100, "默认经") // 指定下标输出 事件

        // 订阅
        .subscribe(new Consumer<String>() { // 下游
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: " + s);
            }
        });


    }
}
