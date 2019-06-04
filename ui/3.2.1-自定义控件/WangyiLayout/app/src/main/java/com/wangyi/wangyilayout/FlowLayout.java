package com.wangyi.wangyilayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends RelativeLayout {
    private LayoutInflater mInflater;
    private int mWidth;//FlowLayout  的宽度
    private List<String> mTags = new ArrayList<String>();
    private boolean mInitialized;
    public FlowLayout(Context context) {
        super(context);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0);
    }
    private void init(Context context, AttributeSet attrs, int defStyle) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewTreeObserver mViewTreeObserber = getViewTreeObserver();
        mViewTreeObserber.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(!mInitialized) {
                    mInitialized = true;
                    drawTags();
                }
            }
        });
    }
    public void addTag(List<String> tags){
        mTags = tags;
        drawTags();
    }

    private void drawTags() {

        removeAllViews();
        float total = 0;
        int index = 1;//现在的位置
        int pindex = index;//相对起点位置
        for(String item:mTags){
            View tagLayout = (View) mInflater.inflate(R.layout.layout_tag, null);
            tagLayout.setId(index);
            TextView tagView = (TextView) tagLayout.findViewById(R.id.tag_txt);
            tagView.setText(item);//设置标签view显示的文字
            tagView.setPadding(10, 5, 10, 5);
            float tagWidth = tagView.getPaint().measureText(item) + 10 * 2;
            LayoutParams tagParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (total + tagWidth < mWidth) {
                tagParams.addRule(RelativeLayout.ALIGN_TOP, pindex);
                tagParams.addRule(RelativeLayout.RIGHT_OF, index - 1);
                if (index > 1) {
                    tagParams.leftMargin = 10;
                    total += 10;

                }
            }else {
                tagParams.addRule(RelativeLayout.BELOW, pindex);
                tagParams.topMargin = 10;
                total = 0;
                pindex = index;//
            }
            total += tagWidth;
            addView(tagLayout, tagParams);//添加到相对布局中
            index++;
        }
    }


}
