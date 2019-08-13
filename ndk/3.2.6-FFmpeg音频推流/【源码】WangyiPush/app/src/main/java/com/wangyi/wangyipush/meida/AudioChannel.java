package com.wangyi.wangyipush.meida;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.wangyi.wangyipush.LivePusher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioChannel {
    private LivePusher mLivePusher;
    private AudioRecord audioRecord;
    private int inputSamples;
    private int channels = 2;
    int channelConfig;
    int minBufferSize;
    private ExecutorService executor;
    private boolean isLiving;
    public AudioChannel(LivePusher livePusher) {
        executor = Executors.newSingleThreadExecutor();
        mLivePusher = livePusher;
        if (channels == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        } else {
            channelConfig = AudioFormat.CHANNEL_IN_MONO;
        }
        mLivePusher.native_setAudioEncInfo(44100, channels);
        //16 位 2个字节
        inputSamples = mLivePusher.getInputSamples() * 2;
//        minBufferSize
         minBufferSize=  AudioRecord.getMinBufferSize(44100,
                channelConfig, AudioFormat.ENCODING_PCM_16BIT)*2;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize < inputSamples ? inputSamples: minBufferSize
        );
    }
    public void startLive() {
        isLiving = true;
        executor.submit(new AudioTeask());
    }



    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void release() {
        audioRecord.release();
    }

    class AudioTeask implements Runnable {


        @Override
        public void run() {
            audioRecord.startRecording();
//    pcm  音频原始数据
            byte[] bytes = new byte[inputSamples];
            while (isLiving) {
                int len = audioRecord.read(bytes, 0, bytes.length);
                mLivePusher.native_pushAudio(bytes);
            }

        }
    }
}
