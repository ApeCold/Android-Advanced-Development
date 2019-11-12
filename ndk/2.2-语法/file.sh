#!/bin/sh
NDK=/root/text.txt
if [ ! -f $NDK ]; then
	mkdir -p /root/wangyi
else
	echo "目录已经存在"
	cat $NDK
fi
	
