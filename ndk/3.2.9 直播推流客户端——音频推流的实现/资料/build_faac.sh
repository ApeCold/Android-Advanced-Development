#!/bin/bash

#
PREFIX=`pwd`/android/armeabi-v7a

TOOLCHAIN=$NDK_ROOT/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
CROSS_COMPILE=$TOOLCHAIN/bin/arm-linux-androideabi

FLAGS="-isysroot $NDK_ROOT/sysroot -isystem $NDK_ROOT/sysroot/usr/include/arm-linux-androideabi -D__ANDROID_API__=17 -g -DANDROID -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -mthumb -Wa,--noexecstack -Wformat -Werror=format-security -std=c++11  -O0  -fPIC"

export CC="$CROSS_COMPILE-gcc --sysroot=$NDK_ROOT/platforms/android-17/arch-arm"
export CFLAGS="$FLAGS"


./configure \
--prefix=$PREFIX \
--host=arm-linux \
--with-pic \
--enable-shared=no  

make clean
make install
