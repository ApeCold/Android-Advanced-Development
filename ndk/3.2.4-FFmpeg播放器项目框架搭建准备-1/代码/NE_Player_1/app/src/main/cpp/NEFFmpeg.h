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

extern "C"{
#include <libavformat/avformat.h>
};


class NEFFmpeg {
public:
    NEFFmpeg(JavaCallHelper *javaCallHelper, char *dataSource);

    ~NEFFmpeg();

    void prepare();
    void _prepare();
private:
    JavaCallHelper *javaCallHelper = 0;
    AudioChannel *audioChannel = 0;
    VideoChannel *videoChannel = 0;
    char *dataSource;
    pthread_t pid_prepare;


};


#endif //NE_PLAYER_1_NEFFMPEG_H
