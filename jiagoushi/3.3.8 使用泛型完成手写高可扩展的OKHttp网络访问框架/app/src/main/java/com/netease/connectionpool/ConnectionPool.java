package com.netease.connectionpool;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Time: 2019-08-22
 * Author: Liudeli
 * Description: 连接池
 */
public class ConnectionPool {

    private final String TAG = ConnectionPool.class.getSimpleName();

    /**
     * 队列，专门去存放（连接对象）
     * 连接池 的存放容器
     */
    private static Deque<HttpConnection>  httpConnectionDeque = null;

    private boolean cleanRunnableFlag;

    /**
     * 检查的机制
     *
     * 每隔一分钟，就去检查，连接池 里面的连接是否可用，如果不可用，就会移除
     *
     */
    private long keepAlive; // 最大允许闲置时间

    public ConnectionPool() {
        this(1,TimeUnit.MINUTES); // 一分钟
        httpConnectionDeque = new ArrayDeque<>();
    }

    public ConnectionPool(long keepAlive, TimeUnit timeUnit) {
        keepAlive = timeUnit.toMillis(keepAlive);
    }


    /**
     * 开启一个线程 专门去检查 连接池里面的 （连接对象）
     *
     * 清理连接池里面的（连接对象）
     */
    private Runnable cleanRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                // 当前的时间
                long nextCheckCleamTime = clean(System.currentTimeMillis());

                if (-1 == nextCheckCleamTime) {
                    return; // while (true) 结束了
                }

                if (nextCheckCleamTime > 0) {
                    // 等待一段时间后，在去检查 是否要去清理
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(nextCheckCleamTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    /**
     * 清理 那些 连接对象
     */
    private long clean(long currentTimeMillis) {
        long idleRecordSave = -1;

        synchronized (this) {

            // 遍历 容器 操作
            Iterator<HttpConnection> iterator = httpConnectionDeque.iterator();
            while (iterator.hasNext()) {
                HttpConnection httpConnection = iterator.next();

                // TODO 我们添加了一个连接对象，超过了（最大闲置时间）就会移除这个连接对象

                // 计算出来的闲置时间
                long idleTime = currentTimeMillis - httpConnection.hastUseTime;

                if (idleTime > keepAlive) { // 大于最大允许的闲置时间了（一分钟）
                    // 移除
                    iterator.remove();

                    // 关闭Socket
                    httpConnection.closeSocket();

                    // 清理 那些 连接对象
                    continue;
                }

                // 得到最长闲置时间 (计算)
                if (idleRecordSave < idleTime) {
                    idleRecordSave = idleTime; // idleRecordSave=10  idleRecordSave=20
                }
            }

            // 出来循环之后，idleRecordSave值计算完毕（闲置时间）
            // keepAlive=60s    idleRecordSave=30  60-30
            if (idleRecordSave >= 0) {
                return (keepAlive - idleRecordSave);
            }

        }

        // 没有计算好，连接池里面没有连接对象，结束掉 线程池中的任务
        return idleRecordSave; // -1
    }

    /**
     * 线程池
     *
     * 复用的决策
     *
     * 线程任务的（内部需要的）
     * LinkedBlockingQueue  链表方式处理的队列
     * SynchronousQueue     队列
     *
     */
    private Executor threadPoolExecutor =
            /**
             * 参数1：0            核心线程数 0
             * 参数2：MAX_VALUE    线程池中最大值
             * 参数3：60           单位值
             * 参数4：秒钟          时 分 秒
             * 参数5：队列          SynchronousQueue
             *
             * 执行任务大于（核心线程数） 启用（60s闲置时间）
             * 60秒闲置时间，没有过，复用之前的线程， 60秒过的，新实例化
             */
            new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "ConnectionPool");
                    thread.setDaemon(true); // 设置为守护线程
                    return thread;
                }
            });

    /**
     * TODO 添加（连接对象）---》 连接池里面
     */
    public synchronized void putConnection(HttpConnection httpConnection) {
        // 一旦put的时候，就要去检查，连接池里面，是否要去清理
        if (!cleanRunnableFlag) { // 如果没有执行，就去执行
            cleanRunnableFlag = true;
            threadPoolExecutor.execute(cleanRunnable); // 1
        }
        httpConnectionDeque.add(httpConnection);

        int size = httpConnectionDeque.size();
        Log.d(TAG, "putConnection: size:" + size);
    }

    /**
     * TODO 获取（连接对象）---》 连接池里面  可用 有效的 （复用）
     * String host, int port --> 查找有效的 连接对象 （复用）
     */
    public HttpConnection getConnection(String host, int port) {
        Iterator<HttpConnection> iterator = httpConnectionDeque.iterator();
        while (iterator.hasNext()) {
            HttpConnection httpConnection = iterator.next();
            if (httpConnection.isConnectionAction(host, port)) {
                // 移除
                iterator.remove();

                // 代表我们找到了 可以复用的
                return httpConnection;
            }
        }
        return null;
    }
}
