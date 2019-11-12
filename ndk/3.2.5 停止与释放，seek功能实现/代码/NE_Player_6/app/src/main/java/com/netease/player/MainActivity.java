package com.netease.player;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private NEPlayer player;
    private SeekBar seekBar;//进度条-与播放总时长挂钩
    private boolean isTouch ;
    private boolean isSeek ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        seekBar = findViewById(R.id.seekBar);

        player = new NEPlayer();
        player.setSurfaceView(surfaceView);
        player.setDataSource(new File(
                Environment.getExternalStorageDirectory() + File.separator + "demo.mp4").getAbsolutePath());
        player.setOnpreparedListener(new NEPlayer.OnpreparedListener() {
            @Override
            public void onPrepared() {
                int duration = player.getDuration();
                //如果是直播，duration是0
                //不为0，可以显示seekbar
                if (duration != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "开始播放");
                        Toast.makeText(MainActivity.this, "开始播放！", Toast.LENGTH_SHORT).show();
                    }
                });
                //播放 调用到native去
                //start play
                player.start();
            }
        });
        player.setOnProgressListener(new NEPlayer.OnProgressListener() {
            @Override
            public void onProgress(final int progress) {
                //progress: 当前的播放进度
                Log.e(TAG, "progress: " + progress);
                //duration

                //非人为干预进度条，让进度条自然的正常播放
                if (!isTouch){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int duration = player.getDuration();
                            Log.e(TAG, "duration: " + duration);
                            if (duration != 0) {
                                if(isSeek){
                                    isSeek = false;
                                    return;
                                }
                                seekBar.setProgress(progress * 100 / duration);
                            }
                        }
                    });
                }
            }
        });
        player.setOnErrorListener(new NEPlayer.OnErrorListener() {
            @Override
            public void onError(final int errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "出错了，错误码：" + errorCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        seekBar.setOnSeekBarChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        player.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    //seek 的核心思路1
    //跟随播放进度自动刷新进度：拿到每个时间点相对总播放时长的百分比进度 progress
    //1，总时间 getDurationNative
    //2，当前播放时间: 随播放进度动态变化的

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        isSeek = true;
        isTouch = false;
        //获取seekbar的当前进度（百分比）
        int seekBarProgress = seekBar.getProgress();
        //将seekbar的进度转换成真实的播放进度
        int duration = player.getDuration();
        int playProgress = seekBarProgress * duration / 100;
        //将播放进度传给底层 ffmpeg

        //seek 的核心思路2
        // 手动拖动进度条，要能跳到指定的播放进度  av_seek_frame
        player.seekTo(playProgress);
    }
}
