# OpenCV 环境配置

标签（空格分隔）： opencv ndk

---

## Visual Studio环境准备
[下载Visual Studio][1]，这里我们下载 **Visual Studio Community 2019** 社区免费版即可。
运行安装程序，勾选 **使用C++的Linux开发** ，

![使用C++的Linux开发](图片\使用C++的Linux开发.png)

单个组件中 **SDK 、库和框架** 下勾选 **Windows 10 SDK** 和 **Windows 通用 C 运行时**，

![使用C++的Linux开发](图片\Windows 10 SDK.png)

**编译器、生成工具和运行时**下勾选 **用于 Windows 的 C++ CMake 工具** 

![使用C++的Linux开发](图片\用于Windows的C++ CMake工具.png)

等待安装完成。

## OpenCV SDK环境准备
[下载OpenCV][2]。这里我们选择最新Release的4.1.1版本，下载Windows端和Android端。

![opencv下载](图片\opencv下载.png)

安装以及解压。

> Mac 可以直接使用 `brew install opencv` 来安装。



## Visual Studio创建OpenCV 的Cmake工程

CMakeLists.txt中添加opencv头文件和库路径，并链接库

```cmake
include_directories("D:/opencv-4.1.1/build/include")
  
link_directories("D:/opencv-4.1.1/build/x64/vc15/lib")

target_link_libraries(OpenCV_Face opencv_world411d) 
```

将opencv中open_world441d动态库添加到生成的可执行文件同目录下

opencv中open_world441d动态库位置：

![opencv中open_world441d动态库](图片\opencv中open_world441d动态库.png)

切换CMake选项：

![切换CMake选项](图片\切换CMake选项.png)

找到工作目录：

![找到工作目录](图片\找到工作目录.png)

拷贝：

![动态库拷贝](图片\动态库拷贝.png)


[1]: blob:https://visualstudio.microsoft.com/406f0ae5-0750-49ed-be39-30be5b1e2614
[2]: https://opencv.org/releases/