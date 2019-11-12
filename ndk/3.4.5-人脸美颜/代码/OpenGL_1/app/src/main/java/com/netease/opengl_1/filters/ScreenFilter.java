package com.netease.opengl_1.filters;

import android.content.Context;

import com.netease.opengl_1.R;

/**
 * 负责直接渲染到屏幕上
 */
public class ScreenFilter extends BaseFilter{

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_fragment);
    }
}

