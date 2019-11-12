//
// Created by Administrator on 2019/8/9.
//

#ifndef NE_PLAYER_1_VIDEOCHANNEL_H
#define NE_PLAYER_1_VIDEOCHANNEL_H


#include "BaseChannel.h"
#include "AudioChannel.h"
#include "macro.h"

extern "C" {
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
};

typedef void (*RenderCallback)(uint8_t *, int, int, int);

class VideoChannel : public BaseChannel {
public:
    VideoChannel(int id, AVCodecContext *codecContext, int fps, AVRational time_base,
                 JavaCallHelper *javaCallHelper);

    ~VideoChannel();

    void start();

    void stop();

    void video_decode();

    void video_play();

    void setRenderCallback(RenderCallback renderCallback);

    void setAudioChannel(AudioChannel *audioChannel);

private:
    pthread_t pid_video_decode;
    pthread_t pid_video_play;
    RenderCallback renderCallback;
    int fps;
    AudioChannel *audioChannel = 0;

};


#endif //NE_PLAYER_1_VIDEOCHANNEL_H
