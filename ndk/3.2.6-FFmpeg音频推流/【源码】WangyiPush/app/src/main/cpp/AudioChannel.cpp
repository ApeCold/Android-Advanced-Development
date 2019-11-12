//
// Created by Administrator on 2019/6/3.
//

#include <pty.h>
#include <jni.h>
#include <cstring>
#include "AudioChannel.h"
#include "faac.h"
#include "librtmp/rtmp.h"
#include "macro.h"

RTMPPacket *AudioChannel::getAudioTag() {
    u_char *buf;
    u_long len;
//     编码器信息     解码
    faacEncGetDecoderSpecificInfo(audioCodec, &buf, &len);
    int bodySizw = 2 + len;
    int bodySize = 2 + len;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    //双声道
    packet->m_body[0] = 0xAF;
    if (mChannels == 1) {
        packet->m_body[0] = 0xAE;
    }
    packet->m_body[1] = 0x00;
    //图片数据
    memcpy(&packet->m_body[2], buf, len);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodySize;
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x11;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;


}
//不断调用
void AudioChannel::encodeData(int8_t *data) {
    int bytelen= faacEncEncode(audioCodec, reinterpret_cast<int32_t *>(data), inputSamples, buffer, maxOutputBytes);
    if (bytelen > 0) {
        RTMPPacket *packet = new RTMPPacket;
        int bodySize = 2 + bytelen;
        RTMPPacket_Alloc(packet, bodySize);
        packet->m_body[0] = 0xAF;
        if (mChannels == 1) {
            packet->m_body[0] = 0xAE;
        }
        //编码出的声音 都是 0x01
        packet->m_body[1] = 0x01;

//        编码之后aac数据  内容    不固定
        memcpy(&packet->m_body[2], buffer, bytelen);


//        aac
        packet->m_hasAbsTimestamp = 0;
        packet->m_nBodySize = bodySize;
        packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
        packet->m_nChannel = 0x11;
        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        audioCallback(packet);
    }

}
void AudioChannel::setAudioCallback(AudioChannel::AudioCallback audioCallback) {
    this->audioCallback = audioCallback;
}
// 初始初始化
void AudioChannel::setAudioEncInfo(int samplesInHZ, int channels){
//    1 最小缓冲大小
    audioCodec=faacEncOpen(samplesInHZ, channels, &inputSamples, &maxOutputBytes);
//    设置参数
    faacEncConfigurationPtr config=faacEncGetCurrentConfiguration(audioCodec);
    config->mpegVersion = MPEG4;
    //lc 标准
    config->aacObjectType = LOW;
    //16位
    config->inputFormat = FAAC_INPUT_16BIT;
    // 编码出原始数据 既不是adts也不是adif
    config->outputFormat = 0;
    faacEncSetConfiguration(audioCodec, config);
    buffer = new u_char[maxOutputBytes];
}

int AudioChannel::getInputSamples() {
    return inputSamples;
}
AudioChannel::~AudioChannel() {
    DELETE(buffer);
    //释放编码器
    if (audioCodec) {
        faacEncClose(audioCodec);
        audioCodec = 0;
    }
}
