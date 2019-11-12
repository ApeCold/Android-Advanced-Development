package com.wangyi.wangyiroom.coonnection;

import android.content.Context;

import com.wangyi.wangyiroom.ChatRoomActivity;
import com.wangyi.wangyiroom.socket.JavaWebSocket;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConnectionManager {
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

    public static PeerConnectionManager getInstance() {
        return ourInstance;
    }
   private PeerConnectionManager() {
        executor = Executors.newSingleThreadExecutor();
    }
    public void initContext(ChatRoomActivity context , EglBase rootEglBase){
        this.context = context;
        this.rootEglBase = rootEglBase;
    }
    public void joinToRoom(JavaWebSocket javaWebSocket,boolean isVideoEnable,
                           ArrayList<String> connections, String myId) {
        this.myId = myId;
        this.videoEnable = isVideoEnable;
//        PeerConnection
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (factory == null) {
                    factory = creteConnectionFactory();
                }

                if (localStream == null) {
                    createLocalStream();
                }
                
            }
        });
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
}
