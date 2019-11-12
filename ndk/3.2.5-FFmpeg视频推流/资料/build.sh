#!/bin/bash

PREFIX=./android/armeabi-v7a

TOOLCHAIN=$NDK_ROOT/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64

FLAGS="-isysroot $NDK_ROOT/sysroot -isystem $NDK_ROOT/sysroot/usr/include/arm-linux-androideabi -D__ANDROID_API__=17 -g -DANDROID -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -mthumb -Wa,--noexecstack -Wformat -Werror=format-security  -O0 -fPIC"

# --disable-cli : 关闭命令行
# 其他和ffmpeg一样
./configure \
--prefix=$PREFIX \
--disable-cli \
--enable-static \
--enable-pic \
--host=arm-linux \
--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
--sysroot=$NDK_ROOT/platforms/android-17/arch-arm \
--extra-cflags="$FLAGS" 



make clean
make install
