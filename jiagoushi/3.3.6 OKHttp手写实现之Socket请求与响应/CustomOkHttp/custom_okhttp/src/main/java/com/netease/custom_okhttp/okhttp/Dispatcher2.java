package com.netease.custom_okhttp.okhttp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Dispatcher2 {

    private int maxRequests = 64; // 同时访问任务，最大限制64个
    private int maxRequestsPerHost = 5; // 同时访问同一个服务器域名，最大限制5个

    private Deque<RealCall2.AsyncCall2> runningAsyncCalls = new ArrayDeque<>(); // 存储运行的队列
    private Deque<RealCall2.AsyncCall2> readyAsyncCalls = new ArrayDeque<>(); // 存储等待的队列

    public void enqueue(RealCall2.AsyncCall2 call) {
        // 同时运行的队列数 必须小于 配置的64   && 同时访问同一个服务器域名 不能超过5个
        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
            runningAsyncCalls.add(call); // 先把任务加入到 运行队列中
            executorService().execute(call); // 然后再执行....
        } else {
            readyAsyncCalls.add(call); // 加入到等待队列中
        }
    }

    /**
     * 缓存方案
     * @return
     */
    public ExecutorService executorService() {
        ExecutorService executorService =
                new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                        60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread thread = new Thread(r);
                                thread.setName("自定义的线程....");
                                thread.setDaemon(false); // 不是守护线程
                                return thread;
                            }
                        });
        return executorService;
    }

    /**
     * 判断AsyncCall2中的Host，在运行的队列中，计数，然后放回
     *
     * 参数AsyncCall2.Request.Host  == runningAsyncCalls.for{AsyncCall2.Request.Host} +1
     *
     * @param call
     * @return
     */
    private int runningCallsForHost(RealCall2.AsyncCall2 call) {
        int count = 0;
        if (runningAsyncCalls.isEmpty()) {
            return 0;
        }

        SocketRequestServer srs = new SocketRequestServer();

        /**
         * 遍历运行队列里面的所有任务，取出任务host == call.host +1
         */
        for (RealCall2.AsyncCall2 runningAsyncCall : runningAsyncCalls) {
            // 取出任务host == call.host
            if (srs.getHost(runningAsyncCall.getRequest()).equals(call.getRequest())) {
                count ++;
            }
        }

        return count;
    }

    /**
     * 1.移除运行完成的任务
     * 2.把等待队列里面所有的任务取出来【执行】  AsyncCall2.run finished
     * @param call2
     */
    public void finished(RealCall2.AsyncCall2 call2) {
        // 当前运行完成的任务 给回收
        runningAsyncCalls.remove(call2);

        // 考虑等待队列里面是否有任务，如果有任务是需要执行的
        if (readyAsyncCalls.isEmpty()) {
            return;
        }

        // 把等待队列中的任务给 移动 运行队列
        for (RealCall2.AsyncCall2 readyAsyncCall : readyAsyncCalls) {
            readyAsyncCalls.remove(readyAsyncCall); // 删除等待队列的任务

            runningAsyncCalls.add(readyAsyncCall); // 把刚刚删除的等待队列任务 加入到 运行队列

            // 开始执行任务
            executorService().execute(readyAsyncCall);
        }
    }
}
