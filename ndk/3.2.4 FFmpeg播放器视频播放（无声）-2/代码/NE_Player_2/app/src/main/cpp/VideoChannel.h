//
// Created by Administrator on 2019/8/9.
//

#ifndef NE_PLAYER_1_VIDEOCHANNEL_H
#define NE_PLAYER_1_VIDEOCHANNEL_H


#include "BaseChannel.h"

class VideoChannel : public BaseChannel {
public:
    VideoChannel(int id);

    ~VideoChannel();

    void start();

    void stop();
};


#endif //NE_PLAYER_1_VIDEOCHANNEL_H
