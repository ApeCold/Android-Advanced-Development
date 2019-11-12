 ## 1. 链接静态库先后顺序不正确，引起的符号定义找不到



```
libavformat/utils.c:513: error: undefined reference to 'av_parser_close'
libavformat/utils.c:518: error: undefined reference to 'avcodec_parameters_to_context'
libavformat/utils.c:525: error: undefined reference to 'avcodec_parameters_to_context'
libavformat/utils.c:4342: error: undefined reference to 'av_parser_close'
libavformat/utils.c:4345: error: undefined reference to 'av_packet_unref'
libavformat/utils.c:4348: error: undefined reference to 'avcodec_free_context'
libavformat/utils.c:4350: error: undefined reference to 'av_bsf_free'
libavformat/utils.c:4354: error: undefined reference to 'av_bsf_free'
libavformat/utils.c:4355: error: undefined reference to 'av_packet_free'
libavformat/utils.c:4360: error: undefined reference to 'avcodec_parameters_free'
libavformat/utils.c:4365: error: undefined reference to 'avcodec_free_context'
...
clang++: error: linker command failed with exit code 1 (use -v to see invocation)
ninja: build stopped: subcommand failed.

```

解决方案：

1. 修改静态库的链接顺序。

   ```cmake
   target_link_libraries(
           native-lib
           avfilter avformat avcodec avutil swresample swscale
           log)
   ```

   

2. 忽略静态库的链接顺序。

   ```cmake
   target_link_libraries(
           native-lib
           -Wl,--start-group
           avcodec avfilter avformat avutil swresample swscale
           -Wl,--end-group
           log)
   ```



## 2. 找不到的z库中的符号 

```
libavformat/http.c:1649: error: undefined reference to 'inflateEnd'
libavformat/http.c:680: error: undefined reference to 'inflateEnd'
libavformat/http.c:681: error: undefined reference to 'inflateInit2_'
libavformat/http.c:686: error: undefined reference to 'zlibCompileFlags'
libavformat/http.c:1428: error: undefined reference to 'inflate'
libavformat/id3v2.c:1023: error: undefined reference to 'uncompress'
libavformat/matroskadec.c:1402: error: undefined reference to 'inflateInit_'
libavformat/matroskadec.c:1410: error: undefined reference to 'inflateEnd'
libavformat/matroskadec.c:1417: error: undefined reference to 'inflate'
libavformat/matroskadec.c:1420: error: undefined reference to 'inflateEnd'
libavformat/mov.c:5125: error: undefined reference to 'uncompress'
libavformat/rtmpproto.c:1077: error: undefined reference to 'inflateInit_'
libavformat/rtmpproto.c:1087: error: undefined reference to 'inflate'
libavformat/swfdec.c:364: error: undefined reference to 'uncompress'
libavformat/swfdec.c:153: error: undefined reference to 'inflateInit_'
libavformat/swfdec.c:121: error: undefined reference to 'inflate'
libavcodec/cscd.c:96: error: undefined reference to 'uncompress'
libavcodec/flashsv.c:126: error: undefined reference to 'inflateInit_'
libavcodec/flashsv.c:259: error: undefined reference to 'deflateInit_'
libavcodec/flashsv.c:261: error: undefined reference to 'deflateBound'
libavcodec/flashsv.c:262: error: undefined reference to 'deflateEnd'
libavcodec/flashsv.c:191: error: undefined reference to 'inflateReset'
libavcodec/flashsv.c:210: error: undefined reference to 'inflateSync'
libavcodec/flashsv.c:160: error: undefined reference to 'deflateInit_'
libavcodec/flashsv.c:166: error: undefined reference to 'deflate'
libavcodec/flashsv.c:167: error: undefined reference to 'deflateEnd'
libavcodec/flashsv.c:169: error: undefined reference to 'inflateReset'
libavcodec/lcldec.c:134: error: undefined reference to 'inflateReset'
libavcodec/mscc.c:168: error: undefined reference to 'inflateReset'
clang++: error: linker command failed with exit code 1 (use -v to see invocation)
ninja: build stopped: subcommand failed.
```

找不到的这些函数名：uncompress、inflateEnd 等都是z库中的函数。

因为 ffmpeg 依赖了z库。编译ffmpeg的时候如果仔细看编译时输出的日志，就可以看到External libraries:
zlib。

```shell
[root@iZky8l27ed9oe3Z ffmpeg-4.1.3]# ./build.sh 
install prefix            ./android/armeabi-v7a2
source path               .
C compiler                /root/android-ndk-r17c/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-gcc
C library                 bionic
host C compiler           gcc
host C library            glibc
ARCH                      arm (armv7-a)
big-endian                no
runtime cpu detection     yes
ARMv5TE enabled           yes
ARMv6 enabled             yes
ARMv6T2 enabled           yes
VFP enabled               yes
NEON enabled              yes
THUMB enabled             yes
debug symbols             yes
strip symbols             yes
optimize for size         yes
optimizations             yes
static                    yes
shared                    no
postprocessing support    no
network support           yes
threading support         pthreads
safe bitstream reader     yes
texi2html enabled         no
perl enabled              yes
pod2man enabled           yes
makeinfo enabled          no
makeinfo supports HTML    no

External libraries:
zlib

```



z库在NDK目录中：ndk路径\platforms\android-xx\arch-arm\usr\lib\libz.so

解决方案：添加z库的依赖。

```cmake
target_link_libraries(
        native-lib
#        -Wl,--start-group
#        avcodec avfilter avformat avutil swresample swscale
#        -Wl,--end-group
        avfilter avformat avcodec avutil swresample swscale
        log
        z
)
```



```
Fatal signal 11 (SIGSEGV), code 1, fault addr 0x65736164 in tid 21246 (.netease.player)
...
backtrace:
     #00 pc 65736164  <unknown>
     #01 pc 000ae7d1  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (_ZN7_JNIEnv12NewGlobalRefEP8_jobject+36)
     #02 pc 000ae777  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (_ZN14JavaCallHelperC2EP7_JavaVMP7_JNIEnvP8_jobject+46)
     #03 pc 000aec35  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (Java_com_netease_player_NEPlayer_native_1prepare+76)
     #04 pc 001327eb  /data/dalvik-cache/arm/data@app@com.netease.player-1@base.apk@classes.dex
```



## 3. JNIEnv跨线程使用问题 

```
Fatal signal 11 (SIGSEGV), code 1, fault addr 0x98 in tid 6944 (.netease.player)	
*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***	
Build fingerprint: Honor/SCL-AL00/hnSCL-Q:5.1.1/HonorSCL-AL00/C00B261:user/release-keys	
Revision: 0	
ABI: arm	
pid: 6240, tid: 6944, name: .netease.player  >>> com.netease.player <<<	
signal 11 (SIGSEGV), code 0 (SI_USER), fault addr 0x98	
     r0 00000000  r1 a0131dd0  r2 fffffa94  r3 00000007	
     r4 b6e08de4  r5 b5259000  r6 00000001  r7 fffffa98	
     r8 b52454d0  r9 b5256c88  sl 00000000  fp a0131c6c	
     ip b5256ca0  sp a0131958  lr b5016017  pc b5015ad8  cpsr 800f0030	
	
backtrace:	
     #00 pc 000afad8  /system/lib/libart.so (_ZN3artL8JniAbortEPKcS1_+47)	
     #01 pc 000b042f  /system/lib/libart.so (_ZN3art9JniAbortFEPKcS1_z+58)	
     #02 pc 000b312f  /system/lib/libart.so (_ZN3art11ScopedCheckC2EP7_JNIEnviPKc+334)	
     #03 pc 000ba801  /system/lib/libart.so (_ZN3art8CheckJNI15CallVoidMethodVEP7_JNIEnvP8_jobjectP10_jmethodIDSt9__va_list+32)	
     #04 pc 000ae943  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (_ZN7_JNIEnv14CallVoidMethodEP8_jobjectP10_jmethodIDz+82)	
     #05 pc 000ae8e9  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (_ZN14JavaCallHelper7onErrorEi+46)	
     #06 pc 000aea33  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (_ZN8NEFFmpeg8_prepareEv+174)	
     #07 pc 000ae97b  /data/app/com.netease.player-1/lib/arm/libnative-lib.so (_Z12task_preparePv+18)	
     #08 pc 00013243  /system/lib/libc.so (_ZL15__pthread_startPv+30)	
     #09 pc 0001125b  /system/lib/libc.so (__start_thread+6)	
```



解决方案：使用 JavaVM 的 AttachCurrentThread函数保证取得当前线程的JNIEnv。

```c++
JNIEnv *env;
javaVM->AttachCurrentThread(&env, NULL);
env->CallVoidMethod(instance, jmid_error, errorCode);
javaVM->DetachCurrentThread();
```

