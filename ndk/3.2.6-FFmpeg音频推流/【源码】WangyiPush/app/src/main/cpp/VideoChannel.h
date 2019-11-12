//
// Created by Administrator on 2019/5/30.
//

#ifndef WANGYIPUSH_VIDEOCHANNEL_H
#define WANGYIPUSH_VIDEOCHANNEL_H


#include "librtmp/rtmp.h"

class VideoChannel {
    typedef void (*VideoCallback)(RTMPPacket* packet);
public:
    void setVideoEncInfo(int width, int heidht, int fps, int bitrate);
    ~VideoChannel();
    void encodeData(int8_t *data);
    void setVideoCallback(VideoCallback videoCallback);
private:
    int mWidth;
    int mHeight;
    int mFps;
    int mBitrate;
    int ySize;
    int uvSize;
    x264_t *videoCodec;
//    一帧
    x264_picture_t *pic_in;
    VideoCallback videoCallback;

    void  sendFrame(int type, uint8_t *payload, int i_payload);
    void sendSpsPps(uint8_t sps[100], uint8_t pps[100], int len, int pps_len);
};
#endif //WANGYIPUSH_VIDEOCHANNEL_H
