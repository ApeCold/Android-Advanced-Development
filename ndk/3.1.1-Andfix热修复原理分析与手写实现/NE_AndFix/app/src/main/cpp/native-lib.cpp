#include <jni.h>
#include <string>
#include <android/log.h>
#include "art_5_1.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_netease_andfix_DexManager_replace(JNIEnv *env, jobject instance, jobject bugMethod,
                                           jobject fixMethod) {
    env->FindClass()
    //ba获得指向被替换的目标方法的指针（有bug的方法的指针）
    art::mirror::ArtMethod *bugArtMethod = reinterpret_cast<art::mirror::ArtMethod *>(env->FromReflectedMethod(
            bugMethod));
    //获得指向新的方法的指针（被修复了的方法的指针）
    art::mirror::ArtMethod *fixArtMethod = reinterpret_cast<art::mirror::ArtMethod *>(env->FromReflectedMethod(
            fixMethod));
    reinterpret_cast<art::mirror::Class *>(fixArtMethod->declaring_class_)->class_loader_ =
            reinterpret_cast<art::mirror::Class *>(bugArtMethod->declaring_class_)->class_loader_; //for plugin classloader
    reinterpret_cast<art::mirror::Class *>(fixArtMethod->declaring_class_)->clinit_thread_id_ =
            reinterpret_cast<art::mirror::Class *>(bugArtMethod->declaring_class_)->clinit_thread_id_;
    reinterpret_cast<art::mirror::Class *>(fixArtMethod->declaring_class_)->status_ =
            reinterpret_cast<art::mirror::Class *>(bugArtMethod->declaring_class_)->status_ - 1;
    reinterpret_cast<art::mirror::Class *>(fixArtMethod->declaring_class_)->super_class_ = 0;
    //把就函数的成员变量替换为新函数的
    bugArtMethod->declaring_class_ = fixArtMethod->declaring_class_;
    bugArtMethod->dex_cache_resolved_methods_ = fixArtMethod->dex_cache_resolved_methods_;
    bugArtMethod->access_flags_ = fixArtMethod->access_flags_;
    bugArtMethod->dex_cache_resolved_types_ = fixArtMethod->dex_cache_resolved_types_;
    bugArtMethod->dex_code_item_offset_ = fixArtMethod->dex_code_item_offset_;
    bugArtMethod->method_index_ = fixArtMethod->method_index_;
    bugArtMethod->dex_method_index_ = fixArtMethod->dex_method_index_;

    bugArtMethod->ptr_sized_fields_.entry_point_from_interpreter_ = fixArtMethod->ptr_sized_fields_.entry_point_from_interpreter_;
    bugArtMethod->ptr_sized_fields_.entry_point_from_jni_ = fixArtMethod->ptr_sized_fields_.entry_point_from_jni_;
    bugArtMethod->ptr_sized_fields_.entry_point_from_quick_compiled_code_ = fixArtMethod->ptr_sized_fields_.entry_point_from_quick_compiled_code_;
    __android_log_print(ANDROID_LOG_ERROR, "AndFix","replace_5_1: %d , %d",
                        static_cast<const char *>(bugArtMethod->ptr_sized_fields_.entry_point_from_quick_compiled_code_),
                        fixArtMethod->ptr_sized_fields_.entry_point_from_quick_compiled_code_);

}