//
// Created by Administrator on 2019/8/9.
//

#ifndef NE_PLAYER_1_NEFFMPEG_H
#define NE_PLAYER_1_NEFFMPEG_H


#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"
#include "macro.h"
#include <cstring>
#include <pthread.h>

extern "C" {
#include <libavformat/avformat.h>
};


class NEFFmpeg {
    friend void *task_stop(void *args);
public:
    NEFFmpeg(JavaCallHelper *javaCallHelper, char *dataSource);

    ~NEFFmpeg();

    void prepare();

    void _prepare();

    void start();

    void _start();

    void setRenderCallback(RenderCallback renderCallback);

    void stop();

private:
    JavaCallHelper *javaCallHelper = 0;
    AudioChannel *audioChannel = 0;
    VideoChannel *videoChannel = 0;
    char *dataSource;
    pthread_t pid_prepare;
    pthread_t pid_start;
    pthread_t pid_stop;
    bool isPlaying;
    AVFormatContext *formatContext = 0;
    RenderCallback renderCallback;
    int duration;
    pthread_mutex_t seekMutex;
public:
    void setDuration(int duration);

    int getDuration() const;
//总播放时长

    void seekTo(int i);
};


#endif //NE_PLAYER_1_NEFFMPEG_H
