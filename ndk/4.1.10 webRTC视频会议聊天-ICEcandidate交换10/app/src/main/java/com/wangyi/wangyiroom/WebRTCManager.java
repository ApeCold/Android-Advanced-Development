package com.wangyi.wangyiroom;

import com.wangyi.wangyiroom.connection.PeerConnectionManager;
import com.wangyi.wangyiroom.socket.JavaWebSocket;

import org.webrtc.EglBase;

public class WebRTCManager {
    private JavaWebSocket webSocket;
    private PeerConnectionManager peerConnectionManager ;
    private String roomId="" ;
    private static final WebRTCManager ourInstance = new WebRTCManager();

    public static WebRTCManager getInstance() {
        return ourInstance;
    }

    private WebRTCManager() {
    }
    public void connect(MainActivity activity,String roomId) {
        this.roomId = roomId;
        webSocket = new JavaWebSocket(activity);
        peerConnectionManager = PeerConnectionManager.getInstance();
        webSocket.connect("wss://47.107.132.117/wss");
    }


    public void joinRoom(ChatRoomActivity chatRoomActivity, EglBase eglBase) {
        peerConnectionManager.initContext(chatRoomActivity, eglBase);
        webSocket.joinRoom(roomId);

    }
}
