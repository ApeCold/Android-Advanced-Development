# 指定cmake最小支持版本
cmake_minimum_required(VERSION 3.4.1)
# 设置头文件目录
include_directories(${CMAKE_SOURCE_DIR}/inc)
# 添加一个库，根据native-lib.cpp源文件编译一个native-lib的动态库
add_library(
        native-lib
        SHARED
        native-lib.cpp)
# 设置第三方so库的路径
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}")

# 查找系统库，这里查找的是系统日志库，并赋值给变量log-lib
#find_library(
#        log-lib
#        log)
# 设置依赖的库（第一个参数必须为目标模块，顺序不能换）
target_link_libraries(
        native-lib
        fmod
        fmodL
        log)
