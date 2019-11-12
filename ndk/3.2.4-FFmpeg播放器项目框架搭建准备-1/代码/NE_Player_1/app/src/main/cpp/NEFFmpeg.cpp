//
// Created by Administrator on 2019/8/9.
//




#include "NEFFmpeg.h"


NEFFmpeg::NEFFmpeg(JavaCallHelper *javaCallHelper, char *dataSource) {
    this->javaCallHelper = javaCallHelper;
//    this->dataSource = dataSource;//?
    //这里的dataSource是从Java传过来的字符串，通过jni接口转成了c++字符串。
    //在jni方法中被释放掉了，导致 dataSource 变成悬空指针的问题（指向一块已经释放了的内存）
    //？？
    //内存拷贝，自己管理它的内存
    //strlen 获取字符串长度，strcpy：拷贝字符串

    //java: "hello"
    //c字符串以 \0 结尾 : "hello\0"
    this->dataSource = new char[strlen(dataSource) + 1];
    strcpy(this->dataSource, dataSource);
}

NEFFmpeg::~NEFFmpeg() {
    DELETE(dataSource);
    DELETE(javaCallHelper);
//    if (javaCallHelper){
//        delete javaCallHelper;
//        javaCallHelper = 0;
//    }
}

/**
 * 准备线程pid_prepare真正执行的函数
 * @param args
 * @return
 */
void *task_prepare(void *args) {
    //打开输入
    NEFFmpeg *ffmpeg = static_cast<NEFFmpeg *>(args);
    //2 dataSource
    ffmpeg->_prepare();
    return 0;//一定一定一定要返回0！！！
}

void NEFFmpeg::_prepare() {

    //1 AVFormatContext **ps
    AVFormatContext *formatContext = avformat_alloc_context();
    AVDictionary *dictionary = 0;
    av_dict_set(&dictionary, "timeout", "10000000", 0);//设置超时时间为10秒，这里的单位是微秒
    int ret = avformat_open_input(&formatContext, dataSource, 0, &dictionary);
    av_dict_free(&dictionary);
    if (ret) {
        //失败 ，回调给java
        LOGE("打开媒体失败：%s", av_err2str(ret));
//        javaCallHelper jni 回调java方法
//        javaCallHelper->onError(ret);
          //可能java层需要根据errorCode来更新UI!
          //2019.8.9
    }
}

/**
 * 播放准备
 * 可能是主线程
 * doc/samples/
 */
void NEFFmpeg::prepare() {
    //可以直接来进行解码api调用吗？
    //xxxxxx。。。。不能！
    //文件：io流问题
    //直播：网络
//    pthread_create： 创建子线程

//pthread_create(pthread_t* __pthread_ptr, pthread_attr_t const* __attr, void* (*__start_routine)(void*), void*);
    pthread_create(&pid_prepare, 0, task_prepare, this);
}
