cmake_minimum_required(VERSION 3.4.1)


file(GLOB SOURCE src/main/cpp/*.cpp)
add_library( # Sets the name of the library.
        wangyiplayer
        SHARED
        ${SOURCE})
find_library( # Sets the name of the path variable.
        log-lib
        log)
include_directories(src/main/cpp/include)

set(my_lib_path  ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})
set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -L${my_lib_path}")
target_link_libraries( # Specifies the target library.
        wangyiplayer
        avfilter avformat avcodec avutil swresample swscale
        ${log-lib}
        z
        OpenSLES
        android
        )