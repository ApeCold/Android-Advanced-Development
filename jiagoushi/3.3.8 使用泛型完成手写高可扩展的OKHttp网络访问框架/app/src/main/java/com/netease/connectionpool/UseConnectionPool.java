package com.netease.connectionpool;

import android.util.Log;

/**
 * Time: 2019-08-22
 * Author: Liudeli
 * Description:
 */
public class UseConnectionPool {

    private final String TAG = UseConnectionPool.class.getSimpleName();

    public void useConnectionPool(ConnectionPool connectionPool, String host, int port) {
        // 模拟
        HttpConnection connection = connectionPool.getConnection(host, port);
        if (connection == null) {
            connection = new HttpConnection(host, port);
            Log.d(TAG, "连接池里面没有 连接对象，需要实例化一个连接对象...");
        } else {
            Log.d(TAG, "useConnectionPool: 复用和一个连接对象");
        }

        // 模拟请求
        // connection.socket .....
        // 把连接对象 加入到  连接池
        connection.hastUseTime = System.currentTimeMillis(); // 更新时间
        connectionPool.putConnection(connection);
        Log.d(TAG, "useConnectionPool: 给服务器发送请求 >>>>>>>> ");
    }

}
