package com.wangyi.wangyiroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.wangyi.wangyiroom.utils.PermissionUtil;
import com.wangyi.wangyiroom.utils.Utils;

import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomActivity extends Activity {
    FrameLayout wrVideoLayout;
    private WebRTCManager webRTCManager;
    private EglBase rootEglBase;
    private VideoTrack localVideoTrack;

    private Map<String, SurfaceViewRenderer> videoViews = new HashMap<>();
    private List<String> persons = new ArrayList<>();
    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, ChatRoomActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initView();
    }

    private void initView() {
        rootEglBase=EglBase.create();
        wrVideoLayout = findViewById(R.id.wr_video_view);
        wrVideoLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT));
        webRTCManager = WebRTCManager.getInstance();
        if (!PermissionUtil.isNeedRequestPermission(this)) {
            webRTCManager.joinRoom(this,rootEglBase);
        }


    }

    /**
     * @param stream  本地流
     * @param userId  自己的ID
     */
    public void onSetLocalStream(MediaStream stream, String userId) {
        List<VideoTrack> videoTracks=stream.videoTracks;
        if (videoTracks.size() > 0) {
            localVideoTrack=  videoTracks.get(0);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addView(userId, stream);
            }
        });


    }

    /**
     * \
     * @param userId    用户ID
     * @param stream    视频流（本地流 或者远端的视频流）
     *  5
     *
     */
    private void addView(String userId, MediaStream stream) {
//        不用SurfaceView  采用webrtc给我们提供的SurfaceViewRenderer
        SurfaceViewRenderer renderer = new SurfaceViewRenderer(this);
//        初始化SurfaceView
        renderer.init(rootEglBase.getEglBaseContext(), null);
//        SCALE_ASPECT_FIT 设置缩放模式 按照View的宽度 和高度设置 ， SCALE_ASPECT_FILL按照摄像头预览的画面大小设置
        renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
//翻转
        renderer.setMirror(true);
//        将摄像头的数据 渲染到surfaceViewrender
        if (stream.videoTracks.size() > 0) {
            stream.videoTracks.get(0).addSink(renderer);
        }

//        会议室  1 + N个人
        videoViews.put(userId, renderer);
        persons.add(userId);
//将SurfaceViewRenderer添加到FrameLayout  width=0  height=0
        wrVideoLayout.addView(renderer);

//        宽度 和高度  size=1
        int size = videoViews.size();
        for (int i = 0; i < size; i++) {
            String peerId = persons.get(i);
            SurfaceViewRenderer renderer1 = videoViews.get(peerId);

            if (renderer1 != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.height = Utils.getWidth(this, size);
                layoutParams.width = Utils.getWidth(this, size);
                layoutParams.leftMargin = Utils.getX(this, size, i);
                layoutParams.topMargin = Utils.getY(this, size, i);
                renderer1.setLayoutParams(layoutParams);
            }
        }


    }

}
