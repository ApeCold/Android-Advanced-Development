# CMakeList.txt: VLPR_1 的 CMake 项目，在此处包括源代码并定义
# 项目特定的逻辑。
#
cmake_minimum_required (VERSION 3.8) 

include_directories("D:/opencv411/build/include")

link_directories("D:/opencv411/build/x64/vc15/lib")

# 将源代码添加到此项目的可执行文件。
add_executable (VLPR_1 "VLPR_1.cpp" PlateRecognize.cpp PlateLocate.cpp SobelLocate.cpp)
 
# TODO: 如有需要，请添加测试并安装目标。
target_link_libraries(VLPR_1 opencv_world411d) 