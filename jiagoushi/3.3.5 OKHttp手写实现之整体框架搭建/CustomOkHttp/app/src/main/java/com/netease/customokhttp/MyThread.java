package com.netease.customokhttp;

/**
 * 守护线程演示
 */
public class MyThread {

    public static void main(String[] args) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
//                    try {
//                        Thread.sleep(0);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } finally {
//                        System.out.println("run...");
//                    }

                    System.out.println("run...");
                }
            }
        };
        // 守护线程
        thread.setDaemon(true);

        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // JVM main所持有的进程 该结束了..

    }

}
