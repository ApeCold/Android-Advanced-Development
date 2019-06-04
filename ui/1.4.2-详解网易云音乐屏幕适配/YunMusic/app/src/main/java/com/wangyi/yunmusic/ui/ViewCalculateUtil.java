package com.wangyi.yunmusic.ui;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewCalculateUtil {
//    界面元素 进行赋值    开发者来说是麻烦的
    public static void setViewLayoutParam(View view, int width, int height, int topMargin, int bottomMargin, int lefMargin, int rightMargin)
    {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if (layoutParams != null)
        {
            if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT)
            {
                layoutParams.width = UIUtils.getInstance().getWidth(width);
            }
            else
            {
                layoutParams.width = width;
            }
            if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT)
            {
                layoutParams.height = UIUtils.getInstance( ).getHeight(height);
            }
            else
            {
                layoutParams.height = height;
            }

            layoutParams.topMargin = UIUtils.getInstance( ).getHeight(topMargin);
            layoutParams.bottomMargin = UIUtils.getInstance( ).getHeight(bottomMargin);
            layoutParams.leftMargin = UIUtils.getInstance( ).getWidth(lefMargin);
            layoutParams.rightMargin = UIUtils.getInstance( ).getWidth(rightMargin);
            view.setLayoutParams(layoutParams);
        }
        else
        {
        }

    }
    public static void setTextSize(TextView view, int size)
    {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.getInstance().getHeight(size));
    }

    public static void setViewLinearLayoutParam(View view, int width, int height)
    {

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT)
        {
            layoutParams.width = UIUtils.getInstance( ).getWidth(width);
        }
        else
        {
            layoutParams.width = width;
        }
        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT)
        {
            layoutParams.height = UIUtils.getInstance( ).getHeight(height);
        }
        else
        {
            layoutParams.height = height;
        }

        view.setLayoutParams(layoutParams);
    }

    public static void setViewGroupLayoutParam(View view, int width, int height)
    {

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT)
        {
            layoutParams.width = UIUtils.getInstance( ).getWidth(width);
        }
        else
        {
            layoutParams.width = width;
        }
        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT)
        {
            layoutParams.height = UIUtils.getInstance( ).getHeight(height);
        }
        else
        {
            layoutParams.height = height;
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 设置LinearLayout中 view的高度宽度
     *
     * @param view
     * @param width
     * @param height
     */
    public static void setViewLinearLayoutParam(View view, int width, int height, int topMargin, int bottomMargin, int lefMargin,
                                                int rightMargin)
    {

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT)
        {
            layoutParams.width = UIUtils.getInstance( ).getWidth(width);
        }
        else
        {
            layoutParams.width = width;
        }
        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT)
        {
            layoutParams.height = UIUtils.getInstance( ).getHeight(height);
        }
        else
        {
            layoutParams.height = height;
        }

        layoutParams.topMargin = UIUtils.getInstance( ).getHeight(topMargin);
        layoutParams.bottomMargin = UIUtils.getInstance( ).getHeight(bottomMargin);
        layoutParams.leftMargin = UIUtils.getInstance( ).getWidth(lefMargin);
        layoutParams.rightMargin = UIUtils.getInstance( ).getWidth(rightMargin);
        view.setLayoutParams(layoutParams);
    }


}
