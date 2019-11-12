package com.netease.custom_okhttp.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

public class Test02 {

    public static void main(String[] args) throws IOException {

        System.out.println("请输入网址，然后回车..."); // www.baidu.com
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputPath = br.readLine();

        URL url = new URL("https://"+inputPath);

        String hostName = url.getHost(); // 域名 主机

        Socket socket = null;

        int port = 0;
        // HTTP  HTTS
        if ("HTTP".equalsIgnoreCase(url.getProtocol())) {
            port = 80;
            socket = new Socket(hostName, port);
        } else if ("HTTPS".equalsIgnoreCase(url.getProtocol())) {
            port = 443;
            socket = SSLSocketFactory.getDefault().createSocket(hostName, port);
        }

        if (socket == null) {
            System.out.println("error");
            return;
        }

        // TODO 写出去  请求
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        /**
         * GET / HTTP/1.1
         * Host: www.baud.com
         */
        bw.write("GET / HTTP/1.1\r\n");
        bw.write("Host: "+hostName+"\r\n\r\n");
        bw.flush();

        // TODO 读取数据 响应
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String readLine = null;
            if ((readLine = bufferedReader.readLine()) != null) {
                System.out.println("响应的数据：" + readLine);
            } else {
                break;
            }
        }
    }

}
