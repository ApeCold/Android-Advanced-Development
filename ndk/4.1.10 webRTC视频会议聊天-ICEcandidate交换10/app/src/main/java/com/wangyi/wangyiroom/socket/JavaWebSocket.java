package com.wangyi.wangyiroom.socket;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wangyi.wangyiroom.ChatRoomActivity;
import com.wangyi.wangyiroom.MainActivity;
import com.wangyi.wangyiroom.connection.PeerConnectionManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class JavaWebSocket {
    PeerConnectionManager peerConnectionManager;
    private static String TAG = "JavaWebSocket";
    private WebSocketClient mWebSocketClient;
    private MainActivity activity;
    public JavaWebSocket(MainActivity activity) {
        this.activity = activity;
    }

    public void connect(String wss) {
        peerConnectionManager = PeerConnectionManager.getInstance();
        URI uri = null;
        try {
            uri = new URI(wss);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(TAG, "onOpen: ");
                ChatRoomActivity.openActivity(activity);
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "onMessage: "+message);
                handleMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "onClose: ");
            }

            @Override
            public void onError(Exception ex) {
                Log.i(TAG, "onError: ");
            }
        };
        if (wss.startsWith("wss")) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrustManagerTest()}, new SecureRandom());
                SSLSocketFactory factory = null;
                if (sslContext != null) {
                    factory = sslContext.getSocketFactory();
                }
                if (factory != null) {
                    mWebSocketClient.setSocket(factory.createSocket());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mWebSocketClient.connect();


    }

    private void handleMessage(String message) {
        Map map = JSON.parseObject(message, Map.class);
        String eventName = (String) map.get("eventName");
//        p2p通信
        if (eventName.equals("_peers")) {
            hanleJoinRoom(map);
        }
    }

    private void hanleJoinRoom(Map map) {
        Map data = (Map) map.get("data");
        JSONArray arr;
        if (data != null) {
            arr = (JSONArray) data.get("connections");
            String js = JSONObject.toJSONString(arr, SerializerFeature.WriteClassName);
            ArrayList<String> connections = (ArrayList<String>)
                    com.alibaba.fastjson.JSONObject.parseArray(js, String.class);
            String myId = (String) data.get("you");
            peerConnectionManager.joinToRoom(this,true, connections, myId);
        }
    }

    //客户端向服务器 发送信息
    /*
            事件类型
            1  __join
            2  __answer
            3  __offer
            4  __ice_candidate
            5  __peer
     */
    public void joinRoom(String roomId) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventName", "__join");
        Map<String, String> childMap = new HashMap<>();
        childMap.put("room", roomId);
        map.put("data", childMap);
        JSONObject object = new JSONObject(map);
        final String jsonString = object.toString();
        Log.d(TAG, "send-->" + jsonString);
        mWebSocketClient.send(jsonString);
    }

    // 忽略证书
    public static class TrustManagerTest implements X509TrustManager {


        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
