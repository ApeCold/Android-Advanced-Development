# CMakeList.txt: OpenCV_Face 的 CMake 项目，在此处包括源代码并定义
# 项目特定的逻辑。
#
cmake_minimum_required (VERSION 3.8)
  
include_directories("D:/opencv-4.1.1/build/include")
  
link_directories("D:/opencv-4.1.1/build/x64/vc15/lib")

# 将源代码添加到此项目的可执行文件。
add_executable (OpenCV_Face "OpenCV_Face.cpp" "OpenCV_Face.h")
  
target_link_libraries(OpenCV_Face opencv_world411d) 

# TODO: 如有需要，请添加测试并安装目标。
