#include <jni.h>
#include <string>
#include "rtmp/libresrtmp.h"
extern "C" JNIEXPORT jstring JNICALL
Java_com_dongnao_livedemo_LiveActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    Java_com_dongnao_livedemo_rtmp_RtmpClient_close(NULL,NULL,NULL);
    return env->NewStringUTF(hello.c_str());
}
