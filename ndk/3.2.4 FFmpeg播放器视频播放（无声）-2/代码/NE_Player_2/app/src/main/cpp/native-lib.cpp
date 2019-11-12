#include <jni.h>
#include <string>
#include "NEFFmpeg.h"

extern "C" {
#include "include/libavutil/avutil.h"
}
JavaVM *javaVM = 0;
JavaCallHelper *javaCallHelper = 0;
NEFFmpeg *ffmpeg = 0;


//extern "C" JNIEXPORT jstring JNICALL
//Java_com_netease_player_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
////    return env->NewStringUTF(hello.c_str());
//    return env->NewStringUTF(av_version_info());
//}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_netease_player_NEPlayer_prepareNative(JNIEnv *env, jobject instance, jstring dataSource_) {
    const char *dataSource = env->GetStringUTFChars(dataSource_, 0);

    javaCallHelper = new JavaCallHelper(javaVM, env, instance);
    ffmpeg = new NEFFmpeg(javaCallHelper, const_cast<char *>(dataSource));
    ffmpeg->prepare();


    env->ReleaseStringUTFChars(dataSource_, dataSource);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_netease_player_NEPlayer_startNative(JNIEnv *env, jobject instance) {
    if (ffmpeg){
        ffmpeg->start();
    }

}
extern "C"
JNIEXPORT void JNICALL
Java_com_netease_player_NEPlayer_staticTest(JNIEnv *env, jclass type) {

    // TODO

}