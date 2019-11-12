package com.wangyi.wangyiroom.utils;

import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
//    屏幕总宽度
    private static int mScreenWidth;
//getx  getY

    public static  int getWidth(Context context,int size) {
        if (mScreenWidth == 0) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                mScreenWidth = manager.getDefaultDisplay().getWidth();
            }
        }
//        只有四个人 及其以下
        if (size <= 4) {
            return mScreenWidth / 2;
        }else {
            return mScreenWidth / 3;
        }

    }
    public static  int getX(Context context,int size, int index) {

        if (mScreenWidth == 0) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                mScreenWidth = manager.getDefaultDisplay().getWidth();
            }
        }
        if (size <= 4) {
//            当会议室只有3个人的时候    第三个人的X值偏移
            if (size == 3 && index == 2) {
                return mScreenWidth / 4;
            }
            return (index % 2) * mScreenWidth / 2;
        } else if (size <= 9) {
//            当size 5个人   3  4
            if (size == 5) {
                if (index == 3) {
                    return mScreenWidth / 6;
                }
                if (index == 4) {
                    return mScreenWidth / 2;
                }
            }


            if (size == 7 && index == 6) {
                return mScreenWidth / 3;
            }
            if (size == 8) {
                if (index == 6) {
                    return mScreenWidth / 6;
                }
                if (index == 7) {
                    return mScreenWidth / 2;
                }
            }
            return (index % 3) * mScreenWidth / 3;
        }

        return 0;

    }


    public static  int getY(Context context,int size, int index) {
        if (mScreenWidth == 0) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                mScreenWidth = manager.getDefaultDisplay().getWidth();
            }
        }
        if (size < 3) {
            return mScreenWidth / 4;
        } else if (size < 5) {
            if (index < 2) {
                return 0;
            } else {
                return mScreenWidth / 2;
            }
        } else if (size < 7) {
            if (index < 3) {
                return mScreenWidth / 2 - (mScreenWidth / 3);
            } else {
                return mScreenWidth / 2;
            }
        } else if (size <= 9) {
            if (index < 3) {
                return 0;
            } else if (index < 6) {
                return mScreenWidth / 3;
            } else {
                return mScreenWidth / 3 * 2;
            }

        }
        return 0;
    }

//    width  2  3

}
