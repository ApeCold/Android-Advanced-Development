//
// Created by Administrator on 2019/8/9.
//

#ifndef NE_PLAYER_1_AUDIOCHANNEL_H
#define NE_PLAYER_1_AUDIOCHANNEL_H


#include "BaseChannel.h"

class AudioChannel : public BaseChannel{
public:
    AudioChannel(int id);

    ~AudioChannel();

    void start();

    void stop();
};


#endif //NE_PLAYER_1_AUDIOCHANNEL_H
