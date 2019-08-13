#include <jni.h>

int test(){
    return 123;
}
// com.netease.ndk_2_3_1;
jint Java_com_netease_ndk_12_13_11_MainActivity_nativeTest(){
    return test();
}