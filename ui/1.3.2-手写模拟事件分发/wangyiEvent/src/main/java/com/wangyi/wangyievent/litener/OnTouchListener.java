package com.wangyi.wangyievent.litener;


import com.wangyi.wangyievent.MotionEvent;
import com.wangyi.wangyievent.View;

public interface OnTouchListener {
    boolean onTouch(View v, MotionEvent event);
}
