package com.netease.connectionpool;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Time: 2019-08-22
 * Author: Liudeli
 * Description: 连接对象
 */
public class HttpConnection {

    private final String TAG = HttpConnection.class.getSimpleName();

    Socket socket;

    long hastUseTime; // 连接对象最后使用时间

    public HttpConnection(final String host, final int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * TODO 各位同学，这里开启任务执行时，所拿到的 socket 会出现null的情况
         */
        /*new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    socket = new Socket(host, port);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    hastUseTime = System.currentTimeMillis();
                }
            }
        }.start();*/
    }

    public boolean isConnectionAction(String host, int port) {
        if (socket == null) {
            return false;
        }

        // socket.getLocalAddress();

        try {
            Log.d(TAG, "isConnectionAction: " + socket.getInetAddress().getHostName());

            if (socket.getPort() == port && socket.getInetAddress().getHostName().equals(host)) {
                return true;
            }
        }catch (Exception e) {
            Log.e(TAG, "isConnectionAction: " + e.getMessage());
        }

        /*return TextUtils.equals(socket.getLocalAddress().getHostName(), host)
                && socket.getPort() == port;*/

        return false;
    }

    public void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "closeSocket: " + e.getMessage());
            }
        }
    }
}