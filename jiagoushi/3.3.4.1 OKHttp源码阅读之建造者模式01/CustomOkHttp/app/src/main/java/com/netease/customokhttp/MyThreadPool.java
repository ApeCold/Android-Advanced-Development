package com.netease.customokhttp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                super.run();
            }
        }.start();

        // Java 1.5 线程池 复用 线程池（线程，如何让这么多线程复用，线程管理工作）
        // Executor
        //  --- ExecutorService
        //      --- AbstractExecutorService
        //            ---- ThreadPoolExecutor
        // ThreadPoolExecutor 学习此类

        // 线程池里面 只有一个核心线程在跑任务
        /**
         * todo 参数一：corePoolSize 核心线程数
         * todo 参数二：maximumPoolSize 线程池非核心线程数 线程池规定大小
         * todo 参数三/四：时间数值keepAliveTime， 单位：时分秒  60s
         *                正在执行的任务Runnable20 < corePoolSize --> 参数三/参数四 才会起作用
         *                作用：Runnable1执行完毕后 闲置60s，如果过了闲置60s,会回收掉Runnable1任务,，如果在闲置时间60s 复用此线程Runnable1
         *
         * todo 参数五：workQueue队列 ：会把超出的任务加入到队列中 缓存起来
         *
         */
//        ExecutorService executorService =
//                new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());


//        ExecutorService executorService =
//                new ThreadPoolExecutor(5, 1, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

//        ExecutorService executorService =
//                new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        // 想实现缓存 线程池方案
        /**
         * todo 参数一：corePoolSize 核心线程数
         * todo 参数二：maximumPoolSize 线程池非核心线程数 线程池规定大小
         * todo 参数三/四：时间数值keepAliveTime， 单位：时分秒  60s
         *                正在执行的任务Runnable20 > corePoolSize --> 参数三/参数四 才会起作用
         *                作用：Runnable1执行完毕后 闲置60s，如果过了闲置60s,会回收掉Runnable1任务,，如果在闲置时间60s 复用此线程Runnable1
         *
         * todo 参数五：workQueue队列 ：会把超出的任务加入到队列中 缓存起来
         *
         */
//        ExecutorService executorService =
//                new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        ExecutorService executorService =
                new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread thread = new Thread();
                                thread.setName("MyOkHttp Dispatcher");
                                thread.setDaemon(false); // 不是守护线程
                                return thread;
                            }
                        });

        for (int i = 0; i < 20; i++) { // 循环第二次 闲置60s, 复用上一个任务
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        System.out.println("当前线程，执行耗时任务，线程是：" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // 》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》


        // Java设计者 考虑到了不用使用线程池的参数配置，提供了API

        ExecutorService executorService1 = Executors.newCachedThreadPool(); // 缓存线程池方案
        executorService1.execute(new Runnable() {
            @Override
            public void run() {
            }
        });

        Executors.newSingleThreadExecutor(); // 线程池里面只有一个 核心线程 最大线程数 也只有一个

        // new ThreadPoolExecutor(5, 5, )
        Executors.newFixedThreadPool(5); // 指定固定大小线程池
    }

}
