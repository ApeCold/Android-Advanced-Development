package com.wangyi.webrtc;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.wangyi.webrtc.bean.MediaType;
import com.wangyi.webrtc.bean.MyIceServer;
import com.wangyi.webrtc.ui.ChatRoomActivity;
import com.wangyi.webrtc.ui.ChatSingleActivity;
import com.wangyi.webrtc.ws.IConnectEvent;
import com.wangyi.webrtc.ws.JavaWebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by dds on 2019/1/7.
 * android_shuai@163.com
 */
public class WebrtcUtil {
    private final static String TAG = "WebrtcUtil";
    private static WebSocketClient mWebSocketClient;

    // turn and stunhttps://47.107.132.117/
    private static MyIceServer[] iceServers = {
            // new MyIceServer("stun:stun.l.google.com:19302"),
            new MyIceServer("stun:47.107.132.117:3478?transport=udp"),
            new MyIceServer("turn:47.107.132.117:3478?transport=udp",
                    "ddssingsong",
                    "123456"),
//            new MyIceServer("turn:129.204.197.215:3478?transport=tcp"),
//            new MyIceServer("stun:129.204.197.215:3478",
//                    "lqf",
//                    "123456"),
    };

    // signalling
//     private static String WSS = "wss://47.254.34.146/wss";
    //本地测试信令地址
    private static String WSS = "ws://129.204.197.215/wss";

    // one to one
    public static void callSingle(Activity activity, String wss, String roomId, boolean videoEnable) {
        if (TextUtils.isEmpty(wss)) {
            wss = WSS;
        }
        WebRTCManager.getInstance().init(wss, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                ChatSingleActivity.openActivity(activity, videoEnable);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
        WebRTCManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO, roomId);
    }

    // Videoconferencing
    public static void call(Activity activity, String wss, String roomId) {
        if (TextUtils.isEmpty(wss)) {
            wss = WSS;
        }
        WebRTCManager.getInstance().init(wss, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                ChatRoomActivity.openActivity(activity);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
        WebRTCManager.getInstance().connect(MediaType.TYPE_MEETING, roomId);
    }

    // test wss
    public static void testWs(String wss) {
        URI uri;
        try {
            uri = new URI(wss);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                Log.e(TAG, "onOpen:");
                mWebSocketClient.send("");
            }

            @Override
            public void onMessage(String message) {
                Log.e(TAG, "onMessage:" + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "onClose:" + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError:");
                Log.e(TAG, ex.toString());
            }
        };

        if (wss.startsWith("wss")) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                if (sslContext != null) {
                    sslContext.init(null, new TrustManager[]{new JavaWebSocket.TrustManagerTest()}, new SecureRandom());
                }

                SSLSocketFactory factory = null;
                if (sslContext != null) {
                    factory = sslContext.getSocketFactory();
                }

                if (factory != null) {
                    mWebSocketClient.setSocket(factory.createSocket());
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mWebSocketClient.connect();
    }


}
