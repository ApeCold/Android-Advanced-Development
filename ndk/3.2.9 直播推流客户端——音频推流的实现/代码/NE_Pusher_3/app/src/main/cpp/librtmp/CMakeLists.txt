cmake_minimum_required(VERSION 3.4.1)

file(GLOB rtmp_src *.c)

#如果是纯c代码环境，必须用CMAKE_C_FLAGS，否则用CMAKE_CXX_FLAGS
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

add_library( 
        rtmp
        STATIC
        ${rtmp_src})

