//
// Created by Administrator on 2019/9/18.
//

#ifndef NE_PUSHER_3_AUDIOCHANNEL_H
#define NE_PUSHER_3_AUDIOCHANNEL_H

#include <faac.h>
#include <sys/types.h>
#include "macro.h"
#include <rtmp.h>

class AudioChannel {
    typedef void (*AudioCallback)(RTMPPacket *packet);

public:
    AudioChannel();

    virtual ~AudioChannel();

    void initAudioEncoder(int sample_rate, int channels);

    int getInputSamples();

    void encodeData(int8_t *data);

    void setAudioCallback(AudioCallback audioCallback);
    RTMPPacket * getAudioSeqHeader();
private:
    u_long inputSamples;
    u_long maxOutputBytes;
    int mChannels;
    faacEncHandle audioEncoder = 0;
    u_char *buffer = 0;
    AudioCallback audioCallback;


};


#endif //NE_PUSHER_3_AUDIOCHANNEL_H
