#include <jni.h>
#include <string>
#include "gif_lib.h"
#include <android/log.h>

#define  LOG_TAG   "gifcompare"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  argb(a, r, g, b) ( ((a) & 0xff) << 24 ) | ( ((b) & 0xff) << 16 ) | ( ((g) & 0xff) << 8 ) | ((r) & 0xff)
typedef struct GifBean {
    //当前帧
    int current_frame;
    //总帧数
    int total_frame;
    //所有帧的时长
    int *delays;
} GifBean;

void drawFrame(GifFileType *gifFileType, GifBean *gifBean, AndroidBitmapInfo info, void *pixels);

long getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_netease_gifcompare_GifNdkDecoder_getWidth(JNIEnv *env, jclass type, jlong gifPointer) {
    GifFileType *gifFileType = (GifFileType *) gifPointer;
    return gifFileType->SWidth;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_netease_gifcompare_GifNdkDecoder_getHeight(JNIEnv *env, jclass type, jlong gifPointer) {
    GifFileType *gifFileType = (GifFileType *) gifPointer;
    return gifFileType->SHeight;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_netease_gifcompare_GifNdkDecoder_loadGif(JNIEnv *env, jclass type, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    int err;
    //调用系统的gif_lib.c的api打开gif，返回的是GifFileType的结构体
    //GifFileType是一个保存gif信息的结构体
    long start = getCurrentTime();
    GifFileType *gifFileType = DGifOpenFileName(path, &err);
    //进行gif的初始化，拿到gif的详细信息
    //调用后gif相关信息就保存到gifFileType中了
    start = getCurrentTime();
    int ret = DGifSlurp(gifFileType);
    if (ret != GIF_OK) {
        LOGE("DGifSlurp 失败：%d", ret);
    }
//    DGifSlurp(gifFileType);
    //下面我们创建一个GifBean结构体，用来保存从GifFileType中读取的信息
    //。。。
    //为GifBean开劈一个内存空间,并初始化内存
    GifBean *gifBean = (GifBean *) malloc(sizeof(GifBean));
    memset(gifBean, 0, sizeof(GifBean));
    //下面就要对这个GifBean的结构体进行赋值保存信息
    //在图形控制扩展块中的第5个字节和第6个字节存放的是每帧的延迟时间，单位是
    // 1/100秒，而唯一能标识这是一个图形扩展块的是第2个字节，固定值0xF9
    ExtensionBlock *ext;
    //遍历每一帧，找到图形控制扩展块的延迟时间
    //给gibBean的delays赋值，它将存放所有的帧的延迟时间
    gifBean->delays = (int *) malloc(sizeof(int) * gifFileType->ImageCount);
    memset(gifBean->delays, 0, sizeof(int) * gifFileType->ImageCount);
    for (int i = 0; i < gifFileType->ImageCount; ++i) {
        //取出每一帧
        SavedImage frame = gifFileType->SavedImages[i];
        //遍历扩展块，取出图形扩展块的表示的时间
        for (int j = 0; j < frame.ExtensionBlockCount; ++j) {
            //找到含有延迟时间的代码块
            //#define GRAPHICS_EXT_FUNC_CODE    0xf9    /* graphics control (GIF89) */
            if (frame.ExtensionBlocks[j].Function == GRAPHICS_EXT_FUNC_CODE) {
                ext = &frame.ExtensionBlocks[j];
                break;
            }
        }
        //拿到延迟时间
        if (ext) {
            //单位是1/100秒：延迟时间1-->10ms
            //ext->Bytes本来存放图形控制扩展块，前面的标识、标签等都是固定值不需要保存
            //所以这里
            //两个字节表示一个int
            // Bytes[2]高八位  Bytes[1]低八位 Bytes[0]保留字节
            int frame_delay = 10 * (ext->Bytes[2] << 8 | ext->Bytes[1]);
            gifBean->delays[i] = frame_delay;
            LOGE("第 %d 帧，延迟时间：%d ms", i, frame_delay);
        }
    }
    LOGE("gif 总帧数: %d  ", gifFileType->ImageCount);
    //总帧数
    gifBean->total_frame = gifFileType->ImageCount;
    //这里是设置一下tag，相当于给view设置一个tag后面获取宽高时会用到
    gifFileType->UserData = gifBean;
    env->ReleaseStringUTFChars(path_, path);
    return (jlong) gifFileType;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_netease_gifcompare_GifNdkDecoder_updateFrame(JNIEnv *env, jclass type, jobject bitmap,
                                                      jlong gifPointer) {
    //强转代表gif的结构体
    GifFileType *gifFileType = (GifFileType *) gifPointer;
    GifBean *gifBean = (GifBean *) gifFileType->UserData;
    //Android中保存Bitmap信息的结构体
    AndroidBitmapInfo info;
    //代表图片的像素数组
    void *pixels;
    //调用后info有值了
    AndroidBitmap_getInfo(env, bitmap, &info);
    //bitmap 转换缓冲区：byte[]
    //锁定bitmap，一副图片是二维数组
    AndroidBitmap_lockPixels(env, bitmap, &pixels);
    //绘制一帧图片
    //todo
    drawFrame(gifFileType, gifBean, info, pixels);
    //绘制完当前帧, 当前帧+1；
    gifBean->current_frame += 1;
    //当绘制到最后一帧
    if (gifBean->current_frame >= gifBean->total_frame) {
        gifBean->current_frame = 0;
    }
    //最后解锁图片
    AndroidBitmap_unlockPixels(env, bitmap);
    return gifBean->delays[gifBean->current_frame];

}

void drawFrame(GifFileType *gifFileType, GifBean *gifBean, AndroidBitmapInfo info, void *pixels) {
    //获取当前帧
    SavedImage savedImage = gifFileType->SavedImages[gifBean->current_frame];
    //获取当前帧信息
    GifImageDesc frameInfo = savedImage.ImageDesc;
    //整幅图片的首地址
    int *px = (int *) pixels;
    //每一行的首地址
    int *line;

    ColorMapObject *colorMapObject = frameInfo.ColorMap;
    if (colorMapObject == NULL) {
        colorMapObject = gifFileType->SColorMap;
    }

    GifByteType gifByteType;
    GifColorType gifColorType;
    //frameInfo.Top： y方向偏移量
    px = (int *) ((char *) px + info.stride * frameInfo.Top);
    //某个像素位置
    int pointPixel;
    //遍历列
    for (int y = frameInfo.Top; y < frameInfo.Top + frameInfo.Height; ++y) {
        line = px;
        //遍历行
        for (int x = frameInfo.Left; x < frameInfo.Left + frameInfo.Width; ++x) {
            pointPixel = (y - frameInfo.Top) * frameInfo.Width + (x - frameInfo.Left);
            //当前帧的像素数据   压缩  lzw算法
            gifByteType = savedImage.RasterBits[pointPixel];
            //需要给每个像素赋颜色
            if (colorMapObject != NULL) {
                gifColorType = colorMapObject->Colors[gifByteType];
                line[x] = argb(255, gifColorType.Red, gifColorType.Green, gifColorType.Blue);
            }
        }
        px = (int *) ((char *) px + info.stride);
    }
}