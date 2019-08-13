
cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(src/main/cpp/librtmp)
add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp
        src/main/cpp/VideoChannel.cpp
        src/main/cpp/AudioChannel.cpp)
include_directories(src/main/cpp/include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}" )
find_library( # Sets the name of the path variable.
        log-lib
        log)
target_link_libraries( # Specifies the target library.
        native-lib
        rtmp
        ${log-lib}
        x264
        faac
        )