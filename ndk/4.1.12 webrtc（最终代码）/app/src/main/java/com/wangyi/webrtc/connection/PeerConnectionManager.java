package com.wangyi.webrtc.connection;


import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.wangyi.webrtc.IViewCallback;
import com.wangyi.webrtc.bean.MediaType;
import com.wangyi.webrtc.bean.MyIceServer;
import com.wangyi.webrtc.ws.IWebSocket;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
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
import org.webrtc.RtpTransceiver;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PeerConnectionManager {

    public final static String TAG = "tuch";

    public static final int VIDEO_RESOLUTION_WIDTH = 320;
    public static final int VIDEO_RESOLUTION_HEIGHT = 240;
    public static final int FPS = 10;
    private static final String VIDEO_CODEC_H264 = "H264";
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";

    private PeerConnectionFactory _factory;
    private MediaStream localStream;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private VideoCapturer captureAndroid;
    private VideoSource videoSource;
    private AudioSource audioSource;

    private ArrayList<String> connectionIdArray;
    private Map<String, Peer> connectionPeerDic;

    private String myId;
    private IViewCallback viewCallback;

    private ArrayList<PeerConnection.IceServer> ICEServers;
    private boolean videoEnable;
    private int mediaType;

    private AudioManager mAudioManager;



    enum Role {Caller, Receiver,}

    private Role role;

    private IWebSocket webSocket;

    private Context context;

    private EglBase rootEglBase;

    @Nullable
    private SurfaceTextureHelper surfaceTextureHelper;

    private final ExecutorService executor;

    public PeerConnectionManager(IWebSocket webSocket, MyIceServer[] iceServers) {
        this.connectionPeerDic = new HashMap<>();
        this.connectionIdArray = new ArrayList<>();
        this.ICEServers = new ArrayList<>();
        this.webSocket = webSocket;
        executor = Executors.newSingleThreadExecutor();
        PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder("stun:47.107.132.117:3478?transport=udp")
                .setUsername("").setPassword("").createIceServer();
        PeerConnection.IceServer iceServer1 = PeerConnection.IceServer.builder("turn:47.107.132.117:3478?transport=udp")
                .setUsername("ddssingsong").setPassword("123456").createIceServer();
        ICEServers.add(iceServer);
        ICEServers.add(iceServer1);
    }

    // 设置界面的回调
    public void setViewCallback(IViewCallback callback) {
        viewCallback = callback;
    }

    // ===================================webSocket回调信息=======================================

    public void initContext(Context context, EglBase eglBase) {
        this.context = context;
        rootEglBase = eglBase;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void onJoinToRoom(ArrayList<String> connections, String myId, boolean isVideoEnable, int mediaType) {
        videoEnable = isVideoEnable;
        this.mediaType = mediaType;
        executor.execute(() -> {
            connectionIdArray.addAll(connections);
            this.myId = myId;
            if (_factory == null) {
                _factory = createConnectionFactory();
            }
            if (localStream == null) {
                createLocalStream();
            }

            createPeerConnections();
            addStreams();
            createOffers();
        });

    }

    public void onRemoteJoinToRoom(String socketId) {
        executor.execute(() -> {
            if (localStream == null) {
                createLocalStream();
            }
            Peer mPeer = new Peer(socketId);
            mPeer.pc.addStream(localStream);
            connectionIdArray.add(socketId);
            connectionPeerDic.put(socketId, mPeer);
        });

    }

    public void onRemoteIceCandidate(String socketId, IceCandidate iceCandidate) {
        executor.execute(() -> {
            Peer peer = connectionPeerDic.get(socketId);
            if (peer != null) {
                peer.pc.addIceCandidate(iceCandidate);
            }
        });

    }

    public void onRemoteIceCandidateRemove(String socketId, List<IceCandidate> iceCandidates) {
        // todo 移除
        executor.execute(() -> Log.d(TAG, "send onRemoteIceCandidateRemove"));

    }

    public void onRemoteOutRoom(String socketId) {
        executor.execute(() -> closePeerConnection(socketId));

    }

    public void onReceiveOffer(String socketId, String description) {
        Log.i(TAG, " 11  PeerConnectionManager  onReceiveOffer: ");
        executor.execute(() -> {
            role = Role.Receiver;
            Peer mPeer = connectionPeerDic.get(socketId);
//            String sessionDescription = description;
//            if (videoEnable) {
//                sessionDescription = preferCodec(description, VIDEO_CODEC_H264, false);
//            }

            SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER, description);

            if (mPeer != null) {
                mPeer.pc.setRemoteDescription(mPeer, sdp);
            }
        });

    }

    public void onReceiverAnswer(String socketId, String sdp) {

        Log.i(TAG, " 10  PeerConnectionManager  onReceiverAnswer: ");
        executor.execute(() -> {
            Peer mPeer = connectionPeerDic.get(socketId);
            SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, sdp);
            if (mPeer != null) {
                mPeer.pc.setRemoteDescription(mPeer, sessionDescription);
            }
        });

    }

    private PeerConnectionFactory createConnectionFactory() {
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .createInitializationOptions());

        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;

        encoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),
                true,
                true);
        decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        return PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(JavaAudioDeviceModule.builder(context).createAudioDeviceModule())
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    // 创建本地流
    private void createLocalStream() {
        this.localStream = _factory.createLocalMediaStream("ARDAMS");
        // 音频
        this.audioSource = _factory.createAudioSource(createAudioConstraints());
        this.localAudioTrack = _factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localStream.addTrack(localAudioTrack);

        if (videoEnable) {
            //创建需要传入设备的名称
            captureAndroid = createVideoCapture();
            // 视频
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = _factory.createVideoSource(captureAndroid.isScreencast());
            if (mediaType == MediaType.TYPE_MEETING) {
                // videoSource.adaptOutputFormat(200, 200, 15);
            }
            captureAndroid.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
            captureAndroid.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
            localVideoTrack = _factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
            localStream.addTrack(localVideoTrack);
        }


        if (viewCallback != null) {
            viewCallback.onSetLocalStream(localStream, myId);
        }

    }

    // 创建所有连接
    private void createPeerConnections() {
        for (Object str : connectionIdArray) {
            Peer peer = new Peer((String) str);
            connectionPeerDic.put((String) str, peer);
        }
    }

    // 为所有连接添加流
    private void addStreams() {

        Log.v(TAG, "2 PeerConnectionManager  为所有连接添加流");
        for (Map.Entry<String, Peer> entry : connectionPeerDic.entrySet()) {
            if (localStream == null) {
                createLocalStream();
            }
            entry.getValue().pc.addStream(localStream);
        }

    }

    // 为所有连接创建offer
    private void createOffers() {
        for (Map.Entry<String, Peer> entry : connectionPeerDic.entrySet()) {
            role = Role.Caller;
            Peer mPeer = entry.getValue();
            mPeer.pc.createOffer(mPeer, offerOrAnswerConstraint());
        }

    }

    // 关闭通道流
    private void closePeerConnection(String connectionId) {
        Peer mPeer = connectionPeerDic.get(connectionId);
        if (mPeer != null) {
            mPeer.pc.close();
        }
        connectionPeerDic.remove(connectionId);
        connectionIdArray.remove(connectionId);
        if (viewCallback != null) {
            viewCallback.onCloseWithId(connectionId);
        }

    }


    //**************************************逻辑控制**************************************
    // 调整摄像头前置后置
    public void switchCamera() {
        if (captureAndroid == null) return;
        if (captureAndroid instanceof CameraVideoCapturer) {
            CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) captureAndroid;
            cameraVideoCapturer.switchCamera(null);
        } else {
            Log.d(TAG, "Will not switch camera, video caputurer is not a camera");
        }

    }

    // 设置自己静音
    public void toggleMute(boolean enable) {
        if (localAudioTrack != null) {
            localAudioTrack.setEnabled(enable);
        }
    }

    public void toggleSpeaker(boolean enable) {
        if (mAudioManager != null) {
            mAudioManager.setSpeakerphoneOn(enable);
        }

    }

    // 退出房间
    public void exitRoom() {
        if (viewCallback != null) {
            viewCallback = null;
        }
        executor.execute(() -> {
            ArrayList myCopy;
            myCopy = (ArrayList) connectionIdArray.clone();
            for (Object Id : myCopy) {
                closePeerConnection((String) Id);
            }
            if (connectionIdArray != null) {
                connectionIdArray.clear();
            }
            if (audioSource != null) {
                audioSource.dispose();
                audioSource = null;
            }

            if (videoSource != null) {
                videoSource.dispose();
                videoSource = null;
            }

            if (captureAndroid != null) {
                try {
                    captureAndroid.stopCapture();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                captureAndroid.dispose();
                captureAndroid = null;
            }

            if (surfaceTextureHelper != null) {
                surfaceTextureHelper.dispose();
                surfaceTextureHelper = null;
            }


            if (_factory != null) {
                _factory.dispose();
                _factory = null;
            }

            if (webSocket != null) {
                webSocket.close();
                webSocket = null;
            }
        });


    }


    private VideoCapturer createVideoCapture() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            videoCapturer = createCameraCapture(new Camera2Enumerator(context));
        } else {
            videoCapturer = createCameraCapture(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(context);
    }


    //**************************************各种约束******************************************/
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";

    private MediaConstraints createAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));
        return audioConstraints;
    }

    private MediaConstraints offerOrAnswerConstraint() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        ArrayList<MediaConstraints.KeyValuePair> keyValuePairs = new ArrayList<>();
        keyValuePairs.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        keyValuePairs.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", String.valueOf(videoEnable)));
        mediaConstraints.mandatory.addAll(keyValuePairs);
        return mediaConstraints;
    }

    //**************************************内部类******************************************/
    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String socketId;

        public Peer(String socketId) {
            this.pc = createPeerConnection();
            this.socketId = socketId;

        }


        //****************************PeerConnection.Observer****************************/
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        }
        //内网状态发生改变   如音视频通话中 4G--->切换成wifi
        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        }

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        //onIceCandidate 调用的时机有两次  第一次在连接到ICE服务器的时候  调用次数是网络中有多少个路由节点(1-n)
// 第二类(有人进入这个房间) 对方 到ICE服务器的 路由节点  调用次数是 视频通话的人在网络中离ICE服务器有多少个路由节点(1-n)
//    IceCandidate  sdp
        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.i(TAG, "onIceCandidate: "+iceCandidate.toString());
            // 发送IceCandidate
            webSocket.sendIceCandidate(socketId, iceCandidate);
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            Log.i(TAG, "onIceCandidatesRemoved:");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.i(TAG, "7  PeerConnectionManager onAddStream: ");
            if (viewCallback != null) {
                viewCallback.onAddRemoteStream(mediaStream, socketId);
            }
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            if (viewCallback != null) {
                viewCallback.onCloseWithId(socketId);
            }
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        @Override
        public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {

        }

        @Override
        public void onTrack(RtpTransceiver transceiver) {

        }


        //****************************SdpObserver****************************/

        @Override
        public void onCreateSuccess(SessionDescription origSdp) {

            Log.v(TAG, "3  PeerConnectionManager  sdp创建成功       " + origSdp.description);
            //设置本地的SDP
//
//            String sdpDescription = origSdp.description;
//            if (videoEnable) {
//                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264, false);
//            }
//
//            final SessionDescription sdp = new SessionDescription(origSdp.type, sdpDescription);
//            final SessionDescription sdp = new SessionDescription(origSdp.type, sdpDescription);
//

            pc.setLocalDescription(Peer.this, origSdp);
        }

        @Override
        public void onSetSuccess() {
            Log.v(TAG, "4  PeerConnectionManager  sdp连接成功        " + pc.signalingState().toString()+"  role  "+role.toString());

            if (pc.signalingState() == PeerConnection.SignalingState.HAVE_REMOTE_OFFER) {
                pc.createAnswer(Peer.this, offerOrAnswerConstraint());
            } else if (pc.signalingState() == PeerConnection.SignalingState.HAVE_LOCAL_OFFER) {
                //判断连接状态为本地发送offer
                if (role == Role.Receiver) {
                    //接收者，发送Answer
                    webSocket.sendAnswer(socketId, pc.getLocalDescription().description);

                } else if (role == Role.Caller) {
                    //发送者,发送自己的offer
                    webSocket.sendOffer(socketId, pc.getLocalDescription().description);
                }

            } else if (pc.signalingState() == PeerConnection.SignalingState.STABLE) {
                // Stable 稳定的
                if (role == Role.Receiver) {
                    Log.i(TAG, "onSetSuccess: 最后一步测试");
                    webSocket.sendAnswer(socketId, pc.getLocalDescription().description);
                }
            }

        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }


        //初始化 RTCPeerConnection 连接管道
        private PeerConnection createPeerConnection() {
            if (_factory == null) {
                _factory = createConnectionFactory();
            }
            // 管道连接抽象类实现方法
            PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(ICEServers);
            return _factory.createPeerConnection(rtcConfig, this);
        }
    }


    // ===================================替换编码方式=========================================
    private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        final String[] lines = sdpDescription.split("\r\n");
        final int mLineIndex = findMediaDescriptionLine(isAudio, lines);
        if (mLineIndex == -1) {
            Log.w(TAG, "No mediaDescription line, so can't prefer " + codec);
            return sdpDescription;
        }
        // A list with all the payload types with name |codec|. The payload types are integers in the
        // range 96-127, but they are stored as strings here.
        final List<String> codecPayloadTypes = new ArrayList<>();
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        final Pattern codecPattern = Pattern.compile("^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$");
        for (String line : lines) {
            Matcher codecMatcher = codecPattern.matcher(line);
            if (codecMatcher.matches()) {
                codecPayloadTypes.add(codecMatcher.group(1));
            }
        }
        if (codecPayloadTypes.isEmpty()) {
            Log.w(TAG, "No payload types with name " + codec);
            return sdpDescription;
        }

        final String newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]);
        if (newMLine == null) {
            return sdpDescription;
        }
        Log.d(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine);
        lines[mLineIndex] = newMLine;
        return joinString(Arrays.asList(lines), "\r\n", true /* delimiterAtEnd */);
    }

    private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
        final String mediaDescription = isAudio ? "m=audio " : "m=video ";
        for (int i = 0; i < sdpLines.length; ++i) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i;
            }
        }
        return -1;
    }

    private static @Nullable
    String movePayloadTypesToFront(
            List<String> preferredPayloadTypes, String mLine) {
        // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
        final List<String> origLineParts = Arrays.asList(mLine.split(" "));
        if (origLineParts.size() <= 3) {
            Log.e(TAG, "Wrong SDP media description format: " + mLine);
            return null;
        }
        final List<String> header = origLineParts.subList(0, 3);
        final List<String> unpreferredPayloadTypes =
                new ArrayList<>(origLineParts.subList(3, origLineParts.size()));
        unpreferredPayloadTypes.removeAll(preferredPayloadTypes);
        // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
        // types.
        final List<String> newLineParts = new ArrayList<>();
        newLineParts.addAll(header);
        newLineParts.addAll(preferredPayloadTypes);
        newLineParts.addAll(unpreferredPayloadTypes);
        return joinString(newLineParts, " ", false /* delimiterAtEnd */);
    }

    private static String joinString(
            Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
        Iterator<? extends CharSequence> iter = s.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter);
        }
        return buffer.toString();
    }

}



