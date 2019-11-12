package com.netease.custom_okhttp.okhttp.chain;

import android.util.Log;

import com.netease.custom_okhttp.okhttp.Request2;
import com.netease.custom_okhttp.okhttp.Response2;
import com.netease.custom_okhttp.okhttp.SocketRequestServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * 连接服务器的拦截器
 */
public class ConnectionServerInterceptor implements Interceptor2 {

    private final String TAG = "conn";

    @Override
    public Response2 doNext(Chain2 chain2) throws IOException {

        // 解析Reqeus
        SocketRequestServer srs = new SocketRequestServer();

        Request2 request2 = chain2.getRequest(); // 更新后的Request  hostName:  post:leng type


        Socket socket = new Socket(srs.getHost(request2), srs.getPort(request2));;

        String result = srs.queryHttpOrHttps(request2.getUrl());
        if (result != null) {
            if ("HTTP".equalsIgnoreCase(result)) {
                // 只能访问HTTP，不能访问HTTPS  S SSL 握手
                socket = new Socket(srs.getHost(request2), srs.getPort(request2));
            } else if ("HTTPS".equalsIgnoreCase(result)){
                // HTTPS
                socket = SSLSocketFactory.getDefault().createSocket(srs.getHost(request2), srs.getPort(request2));
            }
        }

        // todo 请求
        // output
        OutputStream os = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
        String requestAll = srs.getRequestHeaderAll(request2);
        // Log.d(TAG, "requestAll:" + requestAll);
        System.out.println("requestAll:" + requestAll);
        bufferedWriter.write(requestAll); // 给服务器发送请求 --- 请求头信息 所有的
        bufferedWriter.flush(); // 真正的写出去...


        // todo 响应
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // input
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String readerLine = null;
//                while (true) {
//                    try {
//                        if ((readerLine = bufferedReader.readLine()) != null) {
//                            // Log.d(TAG, "服务器响应的:" + readerLine);
//                            System.out.println("服务器响应的:" + readerLine);
//                        } else {
//                            return;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }.start();

        Response2 response2 = new Response2();

        // todo 取出 响应码
        String readLine = bufferedReader.readLine(); // 读取第一行 响应头信息
        // 服务器响应的:HTTP/1.1 200 OK
        String[] strings = readLine.split(" ");
        response2.setStatusCode(Integer.parseInt(strings[1]));

        // todo 取出响应体，只要是空行下面的，就是响应体
        String readerLine = null;
        try {
            while ((readerLine = bufferedReader.readLine()) != null) {
               if ("".equals(readerLine)) {
                   // 读到空行了，就代表下面就是 响应体了
                   response2.setBody(bufferedReader.readLine());
                   break;
               }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // response2.setBody("流程走通....");
        return response2;
    }
}
