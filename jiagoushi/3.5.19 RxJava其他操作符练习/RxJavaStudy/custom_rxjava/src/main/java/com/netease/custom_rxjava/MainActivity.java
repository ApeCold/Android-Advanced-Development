package com.netease.custom_rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

// TODO 我们自己写的RxJava
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO create 操作符
        /*// 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observableEmitter) { // 使用到了，就产生了读写模式
                Log.d(TAG, "subscribe: 上游开始发射...");
                // 发射事件  可写的
                // todo 使用者去调用发射 2
                observableEmitter.onNext(9); //  <? extends Integer> 不可写了   <? super Integer>可写
                observableEmitter.onComplete();
            }
        })
        // Observable<Integer>.subscribe
        .subscribe(new com.netease.custom_rxjava.Observer<Integer>() { // 下游
            // 接口的实现方法
            @Override
            public void onSubscribe() {
                // todo 1
                Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
            }

            // 接口的实现方法
            @Override
            public void onNext(Integer item) {
                // todo 3
                Log.d(TAG, "下游接收事件 onNext: " + item);
            }

            // 接口的实现方法
            @Override
            public void onError(Throwable e) {

            }

            // 接口的实现方法
            @Override
            public void onComplete() {
                // todo 4 最后一步
                Log.d(TAG, "onComplete: 下游接收事件完成√√√√√√√√√√√√√√");
            }
        });*/



        // --------------------------------------------------------------------------


        // TODO just 操作符
        // 上游
        /*Observable.just("A", "B", "C", "D", "E", "F", "G") // todo 内部执行了第二步
        // 订阅
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe() {
                // todo 1
                Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
            }

            @Override
            public void onNext(String item) {
                // todo 3
                Log.d(TAG, "下游接收事件 onNext: " + item);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                // todo 4
                Log.d(TAG, "onComplete: ");
            }
        });*/


        // --------------------------------------------------------------------------


        // TODO map变换操作符 RxJava的核心
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override // todo 6
            public void subscribe(Observer<? super Integer> observableEmitter) { // 使用到了，就产生了读写模式

                // todo observableEmitter == map 包裹了一层

                Log.d(TAG, "subscribe: 上游开始发射...");
                // 发射事件  可写的
                // todo 使用者去调用发射 2
                observableEmitter.onNext(9); //  <? extends Integer> 不可写了   <? super Integer>可写
                observableEmitter.onComplete();
            }
        })

        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                Log.d(TAG, "第1个变换 apply: " + integer);
                return "【" + integer + "】";
            }
        })

        .map(new Function<String, StringBuffer>() {
            @Override
            public StringBuffer apply(String s) {
                Log.d(TAG, "第2个变换 apply: " + s);
                return new StringBuffer().append(s).append("-----------------------");
            }
        })

        .map(new Function<StringBuffer, StringBuffer>() {
            @Override
            public StringBuffer apply(StringBuffer stringBuffer) {
                return new StringBuffer(stringBuffer).append("√√√√√√√√√√√√√√√√√√√√√√√");
            }
        })

        // todo 1
        // ObservableMap<T, R> observableMap = new ObservableMap(source, function);
        // new Observable(observableMap).subscribe
        .subscribe(new com.netease.custom_rxjava.Observer<StringBuffer>() { // 下游
             // 接口的实现方法
             @Override
             public void onSubscribe() {
                 // todo 1
                 Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
             }

             // todo 8
             // 接口的实现方法
             @Override
             public void onNext(StringBuffer item) {
                 // todo 3
                 Log.d(TAG, "下游接收事件 onNext: " + item); // 【9】-----------------------
             }

             // 接口的实现方法
             @Override
             public void onError(Throwable e) {

             }

             // 接口的实现方法
             @Override
             public void onComplete() {
                 // todo 4 最后一步
                 Log.d(TAG, "onComplete: 下游接收事件完成√√√√√√√√√√√√√√");
             }
            });

    }
}
