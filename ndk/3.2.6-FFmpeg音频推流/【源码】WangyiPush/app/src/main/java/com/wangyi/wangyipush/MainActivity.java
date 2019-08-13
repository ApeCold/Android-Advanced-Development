package com.wangyi.wangyipush;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends AppCompatActivity {

    private LivePusher livePusher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        livePusher = new LivePusher(this, 800, 480, 800_000, 10, Camera.CameraInfo.CAMERA_FACING_BACK);
        //  设置摄像头预览的界面
        livePusher.setPreviewDisplay(surfaceView.getHolder());
    }

    public void switchCamera(View view) {
        livePusher.switchCamera();
    }

    public void startLive(View view) {
        livePusher.startLive("rtmp://47.107.132.117/myapp");
    }

    public void stopLive(View view) {
        livePusher.release();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        livePusher.release();
    }
}
