cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(rtmp)
add_subdirectory(restreaming)
#添加动态库
file(GLOB my_source  *.cpp  *.c)
add_library(native-lib SHARED ${my_source})
#设置变量 指向ffmpeg静态库目录
set(my_lib_path ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})
#添加静态库 并设置库路径
#    set_target_properties(x264 PROPERTIES IMPORTED_LOCATION ${my_lib_path}/libx264.a)
#编译链接
target_link_libraries(native-lib log libresrtmp streame jnigraphics android)