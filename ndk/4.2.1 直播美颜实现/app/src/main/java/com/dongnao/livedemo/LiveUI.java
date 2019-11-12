package com.dongnao.livedemo;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongnao.livedemo.core.listener.RESScreenShotListener;
import com.dongnao.livedemo.filter.hardvideofilter.BaseHardVideoFilter;
import com.dongnao.livedemo.ws.StreamLiveCameraView;
import com.dongnao.livedemo.ws.filter.hardfilter.FishEyeFilterHard;
import com.dongnao.livedemo.ws.filter.hardfilter.GPUImageBeautyFilter;
import com.dongnao.livedemo.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;


/**

 */

public class LiveUI implements View.OnClickListener {

    private LiveActivity activity;
    private StreamLiveCameraView liveCameraView;
    private String rtmpUrl = "";
    boolean isFilter = false;
    boolean isMirror = false;

    private Button btnStartStreaming;
    private Button btnStopStreaming;
    private Button btnStartRecord;
    private Button btnStopRecord;

    private ImageView imageView;

    public LiveUI(LiveActivity liveActivity , StreamLiveCameraView liveCameraView , String rtmpUrl) {
        this.activity = liveActivity;
        this.liveCameraView = liveCameraView;
        this.rtmpUrl = rtmpUrl;

        init();
    }

    private void init() {
        btnStartStreaming = (Button) activity.findViewById(R.id.btn_startStreaming);
        btnStartStreaming.setOnClickListener(this);

        btnStopStreaming = (Button) activity.findViewById(R.id.btn_stopStreaming);
        btnStopStreaming.setOnClickListener(this);

        btnStartRecord = (Button) activity.findViewById(R.id.btn_startRecord);
        btnStartRecord.setOnClickListener(this);

        btnStopRecord = (Button) activity.findViewById(R.id.btn_stopRecord);
        btnStopRecord.setOnClickListener(this);



        imageView = (ImageView) activity.findViewById(R.id.iv_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_startStreaming://开始推流
               if(!liveCameraView.isStreaming()){
                   liveCameraView.startStreaming(rtmpUrl);

               }
               break;
           case R.id.btn_stopStreaming://停止推流
               if(liveCameraView.isStreaming()){
                   liveCameraView.stopStreaming();
               }
               break;
           case R.id.btn_startRecord://开始录制
               if(!liveCameraView.isRecord()){
                   Toast.makeText(activity,"开始录制视频", Toast.LENGTH_SHORT).show();
                   liveCameraView.startRecord();
               }
               break;
           case R.id.btn_stopRecord://停止录制
               if(liveCameraView.isRecord()){
                   liveCameraView.stopRecord();
                   Toast.makeText(activity,"视频已成功保存至系统根目录的 Movies/WSLive文件夹中", Toast.LENGTH_LONG).show();
               }
               break;
           default:
               break;
       }
    }
}
