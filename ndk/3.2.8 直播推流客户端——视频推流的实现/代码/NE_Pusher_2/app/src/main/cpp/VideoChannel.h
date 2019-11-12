//
// Created by Administrator on 2019/9/16.
//

#ifndef NE_PUSHER_2_VIDEOCHANNEL_H
#define NE_PUSHER_2_VIDEOCHANNEL_H


#include <x264.h>

class VideoChannel {
typedef void (*VideoCallback)(RTMPPacket *packet);
public:

    VideoChannel();

    virtual ~VideoChannel();

    void initVideoEncoder(int width, int height, int fps, int bitrate);

    void encodeData(int8_t *data);
    void sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len);
    void setVideoCallback(VideoCallback videoCallback);

private:
    int mWidth;
    int mHeight;
    int mFps;
    int mBitrate;
    int y_len;
    int uv_len;
    x264_t *videoEncoder = 0;
    x264_picture_t *pic_in = 0;
    pthread_mutex_t mutex;
    VideoCallback videoCallback;

    void sendFrame(int type, int payload, uint8_t *pPayload);
};


#endif //NE_PUSHER_2_VIDEOCHANNEL_H
