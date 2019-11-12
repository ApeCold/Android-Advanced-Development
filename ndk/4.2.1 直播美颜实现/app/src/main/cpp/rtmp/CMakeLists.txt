#关闭ssl 不支持rtmps
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO"  )
#所有源文件放入 rtmp_source 变量
file(GLOB rtmp_source *.c)
file(GLOB rtmp_source1 librtmp/*.c)
include_directories(librtmp)
#编译静态库
add_library(
             libresrtmp
             STATIC
            ${rtmp_source}
            ${rtmp_source1})