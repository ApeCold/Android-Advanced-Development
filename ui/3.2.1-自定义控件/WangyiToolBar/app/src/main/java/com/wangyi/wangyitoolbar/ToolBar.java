package com.wangyi.wangyitoolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ToolBar extends RelativeLayout {
    private ImageView iv_titlebar_left;
    private ImageView iv_titlebar_right;
    private TextView tv_titlebar_title;
    private int mTextColor = Color.WHITE;
    private String titlename;

    public ToolBar(Context context) {
        super(context);
    }
    public ToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initView(Context context, AttributeSet attrs) {

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ToolBar);
        mTextColor = mTypedArray.getColor(R.styleable.ToolBar_title_text_color, Color.WHITE);
        titlename = mTypedArray.getString(R.styleable.ToolBar_title_text);
        //获取资源后要及时回收
        mTypedArray.recycle();
        LayoutInflater.from(context).inflate(R.layout.titlebar, this, true);
        iv_titlebar_left = (ImageView) findViewById(R.id.iv_titlebar_left);
        iv_titlebar_right = (ImageView) findViewById(R.id.iv_titlebar_right);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        //设置标题文字颜色
        tv_titlebar_title.setTextColor(mTextColor);
        setTitle(titlename);

    }
    public void setLeftListener(OnClickListener onClickListener) {
        iv_titlebar_left.setOnClickListener(onClickListener);
    }

    public void setRightListener(OnClickListener onClickListener) {
        iv_titlebar_right.setOnClickListener(onClickListener);
    }
    public void setTitle(String titlename) {
        if (!TextUtils.isEmpty(titlename)) {
            tv_titlebar_title.setText(titlename);
        }
    }
}
