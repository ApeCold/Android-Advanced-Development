//
// Created by Administrator on 2019/8/9.
//




#include "VideoChannel.h"

VideoChannel::VideoChannel(int id, AVCodecContext *codecContext, int fps) : BaseChannel(id,
                                                                                        codecContext) {
    this->fps = fps;
}

VideoChannel::~VideoChannel() {

}

void *task_video_decode(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    //2 dataSource
    videoChannel->video_decode();
    return 0;//一定一定一定要返回0！！！
}

void *task_video_play(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    //2 dataSource
    videoChannel->video_play();
    return 0;//一定一定一定要返回0！！！
}

void VideoChannel::start() {
    isPlaying = 1;
    //设置队列状态为工作状态
    packets.setWork(1);
    frames.setWork(1);
    //可以进行解码播放？
    //解码
    pthread_create(&pid_video_decode, 0, task_video_decode, this);
    //播放
    pthread_create(&pid_video_play, 0, task_video_play, this);
}

void VideoChannel::stop() {

}

/**
 * 真正视频解码
 */
void VideoChannel::video_decode() {
    AVPacket *packet = 0;
    while (isPlaying) {
        int ret = packets.pop(packet);
        if (!isPlaying) {
            //如果停止播放了，跳出循环 释放packet
            break;
        }
        if (!ret) {
            //取数据包失败
            continue;
        }
        //拿到了视频数据包（编码压缩了的），需要把数据包给解码器进行解码
        ret = avcodec_send_packet(codecContext, packet);
//        releaseAVPacket(&packet);//?
        if (ret) {
            //往解码器发送数据包失败，跳出循环
            break;
        }
        releaseAVPacket(&packet);//释放packet，后面不需要了
        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(codecContext, frame);
        if (ret == AVERROR(EAGAIN)) {
            //重来
            continue;
        } else if (ret != 0) {
            break;
        }
        //ret == 0 数据收发正常,成功获取到了解码后的视频原始数据包 AVFrame ，格式是 yuv
        //对frame进行处理（渲染播放）直接写？
        /**
         * 内存泄漏点2
         * 控制 frames 队列
         */
        while (isPlaying && frames.size() > 100) {
            av_usleep(10 * 1000);
            continue;
        }
        frames.push(frame);
    }//end while
    releaseAVPacket(&packet);
}

void VideoChannel::video_play() {
    AVFrame *frame = 0;
    //要注意对原始数据进行格式转换：yuv > rgba
    // yuv: 400x800 > rgba: 400x800
    uint8_t *dst_data[4];
    int dst_linesize[4];
    SwsContext *sws_ctx = sws_getContext(codecContext->width, codecContext->height,
                                         codecContext->pix_fmt,
                                         codecContext->width, codecContext->height, AV_PIX_FMT_RGBA,
                                         SWS_BILINEAR, NULL, NULL, NULL);
    //给 dst_data dst_linesize 申请内存
    av_image_alloc(dst_data, dst_linesize,
                   codecContext->width, codecContext->height, AV_PIX_FMT_RGBA, 1);
    //根据fps（传入的流的平均帧率来控制每一帧的延时时间）
    //sleep : fps > 时间

    //单位是 : 秒
    double delay_time_per_frame = 1.0 / fps;
    while (isPlaying) {
        int ret = frames.pop(frame);
        if (!isPlaying) {
            //如果停止播放了，跳出循环 释放packet
            break;
        }
        if (!ret) {
            //取数据包失败
            continue;
        }
        //取到了yuv原始数据，下面要进行格式转换
        sws_scale(sws_ctx, frame->data,
                  frame->linesize, 0, codecContext->height, dst_data, dst_linesize);
        //进行休眠
        //每一帧还有自己的额外延时时间

        //extra_delay = repeat_pict / (2*fps)
        double extra_delay = frame->repeat_pict / (2 * fps);
        double real_delay = delay_time_per_frame + extra_delay;
        //单位是：微秒
        av_usleep(real_delay * 1000000);

        //dst_data: AV_PIX_FMT_RGBA格式的数据
        //渲染，回调出去> native-lib里
        // 渲染一副图像 需要什么信息？
        // 宽高！> 图像的尺寸！
        // 图像的内容！(数据)>图像怎么画
        //需要：1，data;2，linesize；3，width; 4， height
        renderCallback(dst_data[0], dst_linesize[0], codecContext->width, codecContext->height);
        releaseAVFrame(&frame);
    }
    releaseAVFrame(&frame);
    isPlaying = 0;
    av_freep(&dst_data[0]);
    sws_freeContext(sws_ctx);
    //MediaCodec
}

void VideoChannel::setRenderCallback(RenderCallback callback) {
    this->renderCallback = callback;
}
