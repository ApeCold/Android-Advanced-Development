# 抖音录制视频预习资料

## Frame Buffer Object

​	帧缓冲对象:**FBO**。默认情况下，我们在`GLSurfaceView`中绘制的结果是显示到屏幕上，然而实际中有很多情况并不需要渲染到屏幕上，这个时候使用**FBO**就可以很方便的实现这类需求。**FBO**可以让我们的渲染不渲染到屏幕上，而是渲染到离屏Buffer中。

​	在上节课中我们创建了一个`ScreenFilter`类用来封装将摄像头数据显示当屏幕上，然而我们需要在显示之前增加各种**"效果"**,如果我们只存在一个`ScreenFilter`，那么所有的**"效果"**都会积压在这个类中，同时也需要大量的`if else`来判断是否开启效果。

![结构1](图片/结构1.png)

​	我们可以将每种效果写到单独的一个`Filter`中去，并且在`ScreenFilter`之前的所有`Filter`都不需要显示到屏幕中,所以在`ScreenFilter`之前都将其使用**FBO**进行缓存。

> 需要注意的是: 摄像头画面经过**FBO**的缓存时候，我们再从**FBO**绘制到屏幕，这时候就不需要再使用`samplerExternalOES`与变换矩阵了。这意味着``ScreenFilter``,使用的采样器就是正常的`sampler2D`，也不需要`#extension GL_OES_EGL_image_external : require`。
>
> 然而在最原始的状态下是没有开启任何效果的，所以ScreenFilter就比较尴尬。
>
> 1、开启效果: 使用`sampler2D`
>
> 2、未开启效果: 使用`samplerExternalOES`
>
> 那么就需要在`ScreenFilter`中使用`if else`来进行判断，但这个判断稍显麻烦，所以这里我选择使用:
>
> ![结构2](图片/结构2.png)
>
> 从摄像头使用的纹理首先绘制到`CameraFilter`的**FBO**中,这样无论是否开启效果`ScreenFilter`都是以`sampler2D`来进行采样。



## MediaCodec

​	MediaCodec是Android 4.1.2(API 16)提供的一套编解码API。它的使用非常简单，它存在一个输入缓冲区与一个输出缓冲区，在编码时我们将数据塞入输入缓冲区，然后从输出缓冲区取出编码完成后的数据就可以了。

![mediacodec](图片/mediacodec.png)



除了直接操作输入缓冲区之外，还有另一种方式来告知`MediaCodec`需要编码的数据，那就是:

```java
public native final Surface createInputSurface();
```

使用此接口创建一个`Surface`，然后我们在这个`Surface`中"作画"，`MediaCodec`就能够自动的编码`Surface`中的“画作”,我们只需要从输出缓冲区取出编码完成之后的数据即可。

​	此前，我们使用OpenGL进行绘画显示在屏幕上，然而想要复制屏幕图像到cpu内存中却不是一件非常轻松的事情。所以我们可以直接将OpenGL显示到屏幕中的图像,同时绘制到`MediaCodec#createInputSurface`当中去。

> PBO(Pixel Buffer Object,像素缓冲对象)通过直接的内存访问(Direct Memory Access,DMA)高速的复制屏幕图像像素数据到CPU内存,但这里我们直接使用`createInputSurface`更简单......
>
> 录制我们在另外一个线程中进行(**录制现场**)，所以录制的EGL环境和显示的EGL环境(`GLSurfaceView`,**显示线程**)是两个独立的工作环境，他们又能够共享上下文资源：**显示线程**中使用的texture等，需要能够在**录制线程**中操作(通过**录制线程**中使用OpenGL绘制到MediaCodec的Surface)。
>
> 在这个线程中我们需要自己来:
>
> 1、配置录制使用的EGL环境(参照GLSurfaceView是怎么配置的)
>
> 2、完成将显示的图像绘制到MediaCodec的Surface中
>
> 3、编码(H.264)与复用(封装mp4)的工作



