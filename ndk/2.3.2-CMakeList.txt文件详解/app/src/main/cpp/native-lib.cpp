#include <jni.h>
#include <string>
#include <android/log.h>
#include <fmod.hpp>


using namespace FMOD;

extern "C" JNIEXPORT jstring JNICALL
Java_com_netease_ndk_12_13_12_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    System *system;
    System_Create(&system);
    unsigned int version;
    system->getVersion(&version);
    __android_log_print(ANDROID_LOG_ERROR, "TEST", "FMOD Version: %08x", version);
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
