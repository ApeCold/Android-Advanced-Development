package com.netease.connectionpool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Executors.newCachedThreadPool();
        Executors.
                newFixedThreadPool(5);*/
    }

    /**
     * Java main method action ....
     * TODO Java main method info:
     */
    public static void main(String[] args) {
        // TODO Java Study

        // ExecutorService executorService = Executors.newCachedThreadPool();// 缓存复用机制

         Executor executorService =
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
                        new LinkedBlockingDeque<Runnable>());


        for (int i = 0; i < 20; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    /*try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    System.out.println("正在允许的 线程名：" + Thread.currentThread().getName());
                }
            });
        }

        // Executors.newSingleThreadExecutor();
    }

    public void request(View view) {
        final ConnectionPool connectionPool = new ConnectionPool();

        new Thread(){
            @Override
            public void run() {
                super.run();
                UseConnectionPool useConnectionPool = new UseConnectionPool();
                useConnectionPool.useConnectionPool(connectionPool,"restapi.amap.com", 80);
                useConnectionPool.useConnectionPool(connectionPool,"restapi.amap.com", 80);
                useConnectionPool.useConnectionPool(connectionPool,"restapi.amap.com", 80);
                useConnectionPool.useConnectionPool(connectionPool,"restapi.amap.com", 80);
                useConnectionPool.useConnectionPool(connectionPool,"restapi.amap.com", 80);
            }
        }.start();
    }
}
