package com.netease.custom_okhttp.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

public class Test01 {

    private static final String PATH = "https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";
    private static final String PATH2 = "http://192.168.1.1";

    public static void main(String[] args) {
        //urlAction();

        // socketHTTP();

        socketHTTPPost();
    }

    private static void urlAction() {
        try {
            URL url = new URL(PATH);

            System.out.println("" + url.getProtocol());
            System.out.println("" + url.getHost());
            System.out.println("" + url.getFile());
            System.out.println("" + url.getQuery());
            System.out.println(url.getPort() + " ---- " + url.getDefaultPort());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    // get request https
    private static void socketHTTPS() {
        try {
            // Socket socket = new Socket("www.baidu.com", 443); //  http:80

            // SSL 握手   访问HTTPS的socket客户端
            Socket socket = SSLSocketFactory.getDefault().createSocket("www.baidu.com", 443);

            // TODO 写出去  请求
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            /**
             * GET / HTTP/1.1
             * Host: www.baud.com
             */
            bw.write("GET / HTTP/1.1\r\n");
            bw.write("Host: www.baidu.com\r\n\r\n");
            bw.flush();

            // TODO 读取数据 响应
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String readLine = null;
                if ((readLine = br.readLine()) != null) {
                    System.out.println("响应的数据：" + readLine);
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get request http
    private static void socketHTTP() {

        /**
         * GET /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1
         * Host: restapi.amap.com
         */

        // HttpURLConnection --->

        try {
            Socket socket = new Socket("restapi.amap.com", 80); //  http:80

            // TODO 写出去  请求
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write("GET /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1\r\n");
            bw.write("Host: restapi.amap.com\r\n\r\n");
            bw.flush();

            // TODO 读取数据 响应
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String readLine = null;
                if ((readLine = br.readLine()) != null) {
                    System.out.println("响应的数据：" + readLine);
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // get request http
    private static void socketHTTPPost() {

        /**
         * POST /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1
         * Content-Length: 48
         * Host: restapi.amap.com
         * Content-Type: application/x-www-form-urlencoded
         *
         * city=110101&key=13cb58f5884f9749287abbead9c658f2
         */

        // HttpURLConnection --->

        try {
            Socket socket = new Socket("restapi.amap.com", 80); //  http:80

            // TODO 写出去  请求
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write("POST /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1\r\n");
            bw.write("Content-Length: 48\r\n");
            bw.write("Content-Type: application/x-www-form-urlencoded\r\n");
            bw.write("Host: restapi.amap.com\r\n\r\n");

            //下面是 POST请求体
            bw.write("city=110101&key=13cb58f5884f9749287abbead9c658f2\r\n");

            bw.flush();

            // TODO 读取数据 响应
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String readLine = null;
                if ((readLine = br.readLine()) != null) {
                    System.out.println("响应的数据：" + readLine);
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
