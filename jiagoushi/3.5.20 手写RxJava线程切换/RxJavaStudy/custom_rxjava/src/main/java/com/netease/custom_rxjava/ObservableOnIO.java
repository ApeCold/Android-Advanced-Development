package com.netease.custom_rxjava;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 上游处理 异步线程的  Observable的子类   [给所有上游 切换异步线程]
public class ObservableOnIO<T>  implements ObservableOnSubscribe<T>{

    // 线程池
    private final static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    // 拿到上一层
    private ObservableOnSubscribe<T> source;

    public ObservableOnIO(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    public void subscribe(final Observer<? super T> observableEmitter) {
       // source.subscribe(observableEmitter); // 是主线程

        // 用线程池的目的，是为了让 所有上游 全部都在 线程池中 异步运行
        EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                source.subscribe(observableEmitter); // 线程池里面所执行的了
            }
        });
    }
}
