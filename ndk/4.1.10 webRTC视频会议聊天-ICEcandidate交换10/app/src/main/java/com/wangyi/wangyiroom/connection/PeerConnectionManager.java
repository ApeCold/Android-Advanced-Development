package com.wangyi.wangyiroom.connection;

import android.util.Log;

import com.wangyi.wangyiroom.ChatRoomActivity;
import com.wangyi.wangyiroom.socket.JavaWebSocket;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConnectionManager {
    private static final String TAG = "PeerConnectionManager";
    private boolean videoEnable;
    private List<PeerConnection> peerConnections;
    private   ExecutorService executor;
    private PeerConnectionFactory factory;
    private ChatRoomActivity context;
    private static final PeerConnectionManager ourInstance = new PeerConnectionManager();
    private EglBase rootEglBase ;
    private AudioSource audioSource;
    private MediaStream localStream;
//    视频轨
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
//    获取摄像头的设备   camera1   camera2
    //    帮助渲染到本地预览
    private SurfaceTextureHelper surfaceTextureHelper;
    private VideoCapturer captureAndroid;
//视频源
    private VideoSource videoSource;
    private String myId;
    //ICE服务器的集合
    private ArrayList<PeerConnection.IceServer> iceServers;
//    会议室的所有用ID
    private ArrayList<String> connectionIdArray;
//   会议室的每一个用户  会对本地实现一个p2p连接Peer（PeerConnection）
    private Map<String, Peer> connectionPeerDic;

//角色  邀请者  被邀请者， 1v1 通话    别人给你音视频通话 Receiver
//    会议室通话   第一次进入会议室 Caller
//        放你已经加入了会议室    别人进入会议室    Receiver
//角色
    private Role role;
    enum Role {Caller, Receiver,}
    public static PeerConnectionManager getInstance() {
        return ourInstance;
    }
   private PeerConnectionManager() {
        executor = Executors.newSingleThreadExecutor();
    }
    public void initContext(ChatRoomActivity context , EglBase rootEglBase){
        this.context = context;
        this.rootEglBase = rootEglBase;
        iceServers = new ArrayList<>();
        this.connectionPeerDic = new HashMap<>();
        this.connectionIdArray = new ArrayList<>();
//        https
        PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder("stun:47.107.132.117:3478?transport=udp")
                .setUsername("").setPassword("").createIceServer();
//        http
        PeerConnection.IceServer iceServer1 = PeerConnection.IceServer.builder("turn:47.107.132.117:3478?transport=udp")
                .setUsername("ddssingsong").setPassword("123456").createIceServer();
        iceServers.add(iceServer);
        iceServers.add(iceServer1);
    }
    public void joinToRoom(JavaWebSocket javaWebSocket,boolean isVideoEnable,
                           ArrayList<String> connections, String myId) {
        this.myId = myId;
        this.videoEnable = isVideoEnable;
//        PeerConnection    情况1    会议室已经有人   的情况
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (factory == null) {
                    factory = creteConnectionFactory();
                }
                if (localStream == null) {
                    createLocalStream();
                }
                connectionIdArray.addAll(connections);
                createPeerConnections();
//                本地的数据流推向会议室的每一个人的能力
                addStreams();
//                发送邀请
                createOffers();
            }
        });
    }

    // 为所有连接添加流
    private void addStreams() {
        Log.v("wangyi", "为所有连接添加流");
        for (Map.Entry<String, Peer> entry : connectionPeerDic.entrySet()) {
            if (localStream == null) {
                createLocalStream();
            }
            entry.getValue().peerConnection.addStream(localStream);

        }
    }
    /**
     * 为所有连接创建offer
     */
    private void createOffers() {
//邀请
        for (Map.Entry<String, Peer> entry : connectionPeerDic.entrySet()) {
//            赋值角色
            role = Role.Caller;
            Peer mPeer = entry.getValue();
//            每一位会议室的人发送邀请，并且传递我的数据类型（音频   视频的选择）  //内部网络请求
            mPeer.peerConnection.createOffer(mPeer, offerOrAnswerConstraint());
        }
    }

    /**
     * 设置传输音视频
     * 音频()
     * 视频(false)
     * @return
     */
    private MediaConstraints offerOrAnswerConstraint() {
//        媒体约束
        MediaConstraints mediaConstraints = new MediaConstraints();
        ArrayList<MediaConstraints.KeyValuePair> keyValuePairs = new ArrayList<>();
//        音频  必须传输
        keyValuePairs.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
//        videoEnable
        keyValuePairs.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", String.valueOf(videoEnable)));

        mediaConstraints.mandatory.addAll(keyValuePairs);
        return mediaConstraints;
    }
    /**
     * 建立对会议室每一个用户的连接
     */
    private void createPeerConnections() {

        for (String id : connectionIdArray) {
            Peer peer = new Peer(id);
            connectionPeerDic.put(id, peer);
        }
    }

    private void createLocalStream() {
        localStream = factory.createLocalMediaStream("ARDAMS");

//        音频
        audioSource=factory.createAudioSource(createAudioConstraints());
//        采集音频
        localAudioTrack= factory.createAudioTrack("ARDAMSa0", audioSource);
        localStream.addTrack(localAudioTrack);
        if (videoEnable) {
//            视频源
            captureAndroid=createVideoCapture();
            videoSource=factory.createVideoSource(captureAndroid.isScreencast());
            surfaceTextureHelper= SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//            初始化captureAndroid
            captureAndroid.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
//            摄像头预览的宽度 高度   帧率
            captureAndroid.startCapture(320, 240, 10);
//视频轨
            localVideoTrack=factory.createVideoTrack("ARDAMSv0", videoSource);

            localStream.addTrack(localVideoTrack);
            if (context != null) {
                for (int i = 0; i < 5; i++) {
                    context.onSetLocalStream(localStream,myId+i);
                }
            }
        }
    }

    private VideoCapturer createVideoCapture() {
        VideoCapturer videoCapturer = null;
        if (Camera2Enumerator.isSupported(context)) {
            Camera2Enumerator enumerator = new Camera2Enumerator(context);
           videoCapturer = createCameraCapture(enumerator);
        }else {
            Camera1Enumerator enumerator = new Camera1Enumerator(true);
            videoCapturer = createCameraCapture(enumerator);
        }
        return    videoCapturer;
    }

    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {

//        0   1  front
        String[] deviceNames = enumerator.getDeviceNames();
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer= enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }

        }
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer= enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

       return null;
    }

    //    googEchoCancellation   回音消除
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    //    googNoiseSuppression   噪声抑制
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";

    //    googAutoGainControl    自动增益控制
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    //    googHighpassFilter     高通滤波器
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";


    private MediaConstraints createAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT
                , "true"));

        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
        return audioConstraints;
    }

    private PeerConnectionFactory creteConnectionFactory() {
        VideoEncoderFactory encoderFactory;
        VideoDecoderFactory decoderFactory;
//        其他参数设置成默认的
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions());
        encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        decoderFactory=new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        return PeerConnectionFactory.builder().setOptions(options)
                .setAudioDeviceModule(JavaAudioDeviceModule.builder(context)
                        .createAudioDeviceModule()).setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory).createPeerConnectionFactory();

    }

    private class Peer  implements SdpObserver, PeerConnection.Observer {
//myid  跟远端用户之间的连接
        private PeerConnection peerConnection;
//        socket是其他用的id
        private String socketId;
        public Peer(String socketId) {
            PeerConnection.RTCConfiguration rtcConfiguration = new PeerConnection.RTCConfiguration(iceServers);
            peerConnection = factory.createPeerConnection(rtcConfiguration, this);
        }

        //内网状态发生改变   如音视频通话中 4G--->切换成wifi
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }
        //连接上了ICE服务器
        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }
        //onIceCandidate 调用的时机有两次  第一次在连接到ICE服务器的时候  调用次数是网络中有多少个路由节点(1-n)
// 第二类(有人进入这个房间) 对方 到ICE服务器的 路由节点  调用次数是 视频通话的人在网络中离ICE服务器有多少个路由节点(1-n)

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
//            socket-----》   传递
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

        }
//p2p建立成功之后   mediaStream（视频流  音段流）
        @Override
        public void onAddStream(MediaStream mediaStream) {

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

        }
//------------------------------------SDPobserver-------------------------------------------------
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.i(TAG, "onCreateSuccess: ");
        }

        @Override
        public void onSetSuccess() {
            Log.i(TAG, "onSetSuccess: ");
        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }
    }
}
