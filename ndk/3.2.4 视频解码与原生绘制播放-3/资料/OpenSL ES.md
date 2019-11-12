# OpenSL ES

---
[TOC]
## 概述
OpenSL ES 是无授权费、跨平台、针对嵌入式系统精心优化的硬件音频加速API。该库都允许使用C或C ++来实现高性能，低延迟的音频操作。
Android的OpenSL ES库同样位于NDK的platforms文件夹内。关于OpenSL ES的使用可以进入ndk-sample查看[native-audio工程][1]:

## 开发流程七步曲

 1. 创建引擎并获取引擎接口
 2. 设置混音器
 3. 创建播放器
 4. 设置播放回调函数
 5. 设置播放器状态为播放状态
 6. 手动激活回调函数
 7. 释放

### 1、创建引擎并获取引擎接口
```C++
SLresult result;
// 1.1 创建引擎对象：SLObjectItf engineObject
result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
if (SL_RESULT_SUCCESS != result) {
    return;
}
// 1.2 初始化引擎
result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
if (SL_RESULT_SUCCESS != result) {
    return;
}
// 1.3 获取引擎接口 SLEngineItf engineInterface
result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineInterface);
if (SL_RESULT_SUCCESS != result) {
    return;
}
```

### 2、设置混音器
```C++
// 2.1 创建混音器：SLObjectItf outputMixObject
result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0,
                                             0, 0);
if (SL_RESULT_SUCCESS != result) {
    return;
}
// 2.2 初始化混音器
result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
if (SL_RESULT_SUCCESS != result) {
    return;
}
//不启用混响可以不用获取混音器接口
// 获得混音器接口
//result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
//                                         &outputMixEnvironmentalReverb);
//if (SL_RESULT_SUCCESS == result) {
//设置混响 ： 默认。
//SL_I3DL2_ENVIRONMENT_PRESET_ROOM: 室内
//SL_I3DL2_ENVIRONMENT_PRESET_AUDITORIUM : 礼堂 等
//const SLEnvironmentalReverbSettings settings = SL_I3DL2_ENVIRONMENT_PRESET_DEFAULT;
//(*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
//       outputMixEnvironmentalReverb, &settings);
//}
```

### 3、创建播放器
```C++
//3.1 配置输入声音信息
//创建buffer缓冲类型的队列 2个队列
SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
                                                   2};
//pcm数据格式
//SL_DATAFORMAT_PCM：数据格式为pcm格式
//2：双声道
//SL_SAMPLINGRATE_44_1：采样率为44100
//SL_PCMSAMPLEFORMAT_FIXED_16：采样格式为16bit
//SL_PCMSAMPLEFORMAT_FIXED_16：数据大小为16bit
//SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT：左右声道（双声道）
//SL_BYTEORDER_LITTLEENDIAN：小端模式
SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1,
                               SL_PCMSAMPLEFORMAT_FIXED_16,
                               SL_PCMSAMPLEFORMAT_FIXED_16,
                               SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                               SL_BYTEORDER_LITTLEENDIAN};

//数据源 将上述配置信息放到这个数据源中
SLDataSource audioSrc = {&loc_bufq, &format_pcm};

//3.2 配置音轨（输出）
//设置混音器
SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
SLDataSink audioSnk = {&loc_outmix, NULL};
//需要的接口 操作队列的接口
const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
const SLboolean req[1] = {SL_BOOLEAN_TRUE};
//3.3 创建播放器
result = (*engineInterface)->CreateAudioPlayer(engineInterface, &bqPlayerObject, &audioSrc, &audioSnk, 1, ids, req);
if (SL_RESULT_SUCCESS != result) {
    return;
}
//3.4 初始化播放器：SLObjectItf bqPlayerObject
result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
if (SL_RESULT_SUCCESS != result) {
    return;
}
//3.5 获取播放器接口：SLPlayItf bqPlayerPlay
result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
if (SL_RESULT_SUCCESS != result) {
    return;
}
```

### 4、设置播放回调函数
```C++
//4.1 获取播放器队列接口：SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue
(*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bqPlayerBufferQueue);

//4.2 设置回调 void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
(*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, this);
```

```C++
//4.3 创建回调函数
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    ...
}
```

### 5、设置播放器状态为播放状态
```C++
(*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
```

### 6、手动激活回调函数
```C++
bqPlayerCallback(bqPlayerBufferQueue, this);
```

### 7、释放
```C++
//7.1 设置停止状态
if (bqPlayerPlay) {
    (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_STOPPED);
    bqPlayerPlay = 0;
}
//7.2 销毁播放器
if (bqPlayerObject) {
    (*bqPlayerObject)->Destroy(bqPlayerObject);
    bqPlayerObject = 0;
    bqPlayerBufferQueue = 0;
}
//7.3 销毁混音器
if (outputMixObject) {
    (*outputMixObject)->Destroy(outputMixObject);
    outputMixObject = 0;
}
//7.4 销毁引擎
if (engineObject) {
    (*engineObject)->Destroy(engineObject);
    engineObject = 0;
    engineInterface = 0;
}
```

[1]: https://github.com/googlesamples/android-ndk/blob/master/native-audio/app/src/main/cpp/native-audio-jni.c