cmake_minimum_required(VERSION 3.4.1)

# opencv
include_directories(include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}")

# seeta
add_subdirectory(FaceAlignment)

add_library(
        native-lib
        SHARED
        native-lib.cpp
        FaceTrack.cpp
)

target_link_libraries( 
        native-lib
        opencv_java4
        seeta_fa_lib
        log)