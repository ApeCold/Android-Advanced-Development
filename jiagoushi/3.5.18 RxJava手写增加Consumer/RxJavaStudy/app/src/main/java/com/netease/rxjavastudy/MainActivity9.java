package com.netease.rxjavastudy;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * todo 背压模式
 */
public class MainActivity9 extends AppCompatActivity {

    private final String TAG = MainActivity9.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    Subscription subscription;

    public void r01(View view) {

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                // todo 1 上游不停的发射大量事件
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    e.onNext(i); // todo 1
                }
                e.onComplete();
            }
        },
         // ERROR  放入缓存池，如果池子满了  水缸  max 128
         // BackpressureStrategy.ERROR // todo 上游不停的发射大量事件，下游阻塞了 处理不过来，放入缓存池，如果池子满了，就会抛出异常


         BackpressureStrategy.BUFFER // todo  上游不停的发射大量事件，下游阻塞了 处理不过来，放入缓存池，”等待“下游来接收事件处理


         // BackpressureStrategy.DROP //  todo   上游不停的发射大量事件，下游阻塞了 处理不过来，放入缓存池，如果池子满了，就会把后面发射的事件丢弃

                // （1 ~ 5000  池子满了4000，   4001 ~ 5000丢弃）
         // BackpressureStrategy.LATEST // todo 上游不停的发射大量事件，下游阻塞了 处理不过来，只存储 128个事件
         )

                // todo 添加之后，就是异步的
         .subscribeOn(Schedulers.io()) // 给上游分配异步线程
         .observeOn(AndroidSchedulers.mainThread())     // 给下游分配 主线程

         // 订阅
        .subscribe(

                // 下游的简化版
                /*new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }*/

                // 完整版本的下游 观察者
                new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        // 如果同步的 不执行此s.request();，（等待下游，发现下游没有去处理）会抛出异常， 外界在调用subscription.request(10); 无效果
                        // 如果是异步的，不执行此s.request();，不会发生异常（上游不会等待下游）不会发生异常， 外界在调用subscription.request(10); 是ok的
                        // s.request(5); // 只请求输出 5次，给下游打印
                        // s.request(100);  // 只请求输出 100次，给下游打印
                        // s.request(500); // 只请求给下游输出 500个事件
                        // s.request(128); // 取出129给事件，给下游
                        // s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {

                        // todo 2 下游阻塞了 处理不过来
                        try {
                            Thread.currentThread().sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        // TODO 一旦下游处理了一次上游的事件，缓存池 - 1
                        Log.d(TAG, "onNext: " + integer);
                    }

                    // onError: create: could not emit value due to lack of requests  上游还有剩余的事件，无法处理，因为没有去请求
                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                }
        );
    }

    /**
     * 外界调用 subscription.request(10); 异步的才有效
     * @param view
     */
    public void r02(View view) {

        /**
         *  todo 同步的 之前打印不出来，同步的 需要等待 下游处理后，然后再发射后面的事件，由于等待下游 没有request，所以就抛出异常 create: could not emit value due to lack of requests
         *   之后  我们再 r02 方法中 点击 执行 subscription.request(1); 没效果
         *
         * todo 异步的：上游不停的发射， subscription.request(1); 就可以取出来了
         */

        if (subscription != null)
            // subscription.request(1); // 点击一下按钮，就接收一个，取出来 给下游处理
            subscription.request(10);

    }

    public void r03(View view) {

        // Observable.just()
        // Flowable.just();

        // Observable.fromArray()
        // Flowable.fromArray()

    }
}
