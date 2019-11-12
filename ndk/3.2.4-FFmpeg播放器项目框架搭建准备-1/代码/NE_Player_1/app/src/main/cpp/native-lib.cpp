#include <jni.h>
#include <string>
#include "NEFFmpeg.h"

extern "C"{
#include "include/libavutil/avutil.h"
}

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_netease_player_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
////    return env->NewStringUTF(hello.c_str());
//    return env->NewStringUTF(av_version_info());
//}
extern "C"
JNIEXPORT void JNICALL
Java_com_netease_player_NEPlayer_prepareNative(JNIEnv *env, jobject instance, jstring dataSource_) {
    const char *dataSource = env->GetStringUTFChars(dataSource_, 0);

    JavaCallHelper *javaCallHelper = new JavaCallHelper();
    NEFFmpeg *ffmpeg = new NEFFmpeg(javaCallHelper, const_cast<char *>(dataSource));
    ffmpeg->prepare();


    env->ReleaseStringUTFChars(dataSource_, dataSource);
}