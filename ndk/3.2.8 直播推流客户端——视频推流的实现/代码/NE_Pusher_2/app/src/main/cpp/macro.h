//
// Created by Administrator on 2019/8/9.
//

#ifndef NE_PLAYER_1_MACRO_H
#define NE_PLAYER_1_MACRO_H

#include <android/log.h>

//if (dataSource){
//delete dataSource;
//dataSource = 0;
//}

//定义释放的宏函数
#define DELETE(object) if(object){delete object; object = 0;}

//定义日志打印宏函数
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "NEPUSHER",__VA_ARGS__)




#endif //NE_PLAYER_1_MACRO_H
