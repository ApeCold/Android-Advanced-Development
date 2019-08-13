#### Android播放音频的几种方式介绍

##### 1.使用MediaPlayer播放音频

```
//直接创建，不需要设置setDataSource
MediaPlayer mMediaPlayer；
mMediaPlayer=MediaPlayer.create(this, R.raw.audio); 
mMediaPlayer.start();
```



##### 2 使用AudioTrack播放音频

```
	 AudioTrack audio = new AudioTrack(
     AudioManager.STREAM_MUSIC, // 指定流的类型
     32000, // 设置音频数据的採样率 32k，假设是44.1k就是44100
     AudioFormat.CHANNEL_OUT_STEREO, // 设置输出声道为双声道立体声，而CHANNEL_OUT_MONO类型是单声道
     AudioFormat.ENCODING_PCM_16BIT, // 设置音频数据块是8位还是16位。这里设置为16位。
```

##### 3 使用OpenSL ES播放

```
/混音器
SLObjectItf outputMixObject = NULL;//用SLObjectItf创建混音器接口对象
SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;////创建具体的混音器对象实例
 
result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, mids, mreq);//利用引擎接口对象创建混音器接口对象
result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);//实现（Realize）混音器接口对象
result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB, &outputMixEnvironmentalReverb);//利用混音器接口对象初始化具体混音器实例
```

#### 4 Android native操作Java的官方例子

<https://github.com/googlesamples/android-ndk>





#### 5 音视频同步



1  以帧率来做同步

FFmpeg.cpp

```
 AVRational frame_rate=stream->avg_frame_rate;
 //     int fps = frame_rate.num / frame_rate.den;
            int fps = av_q2d(frame_rate);
```

​	VideoChannel.cpp

```
double frame_delays = 1.0 / fps;
av_usleep(frame_delays * 1000000);
```