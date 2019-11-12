package com.netease.skin.library.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.netease.skin.library.R;
import com.netease.skin.library.core.ViewsMatch;
import com.netease.skin.library.model.AttrsBean;

/**
 * 继承TextView兼容包，9.0源码中也是如此
 * 参考：AppCompatViewInflater.java
 * 86行 + 138行 + 206行
 */
public class SkinnableImageView extends AppCompatImageView implements ViewsMatch {

    private AttrsBean attrsBean;

    public SkinnableImageView(Context context) {
        this(context, null);
    }

    public SkinnableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();

        // 根据自定义属性，匹配控件属性的类型集合，如：src
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableImageView,
                defStyleAttr, 0);
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableImageView);
        // 这一句回收非常重要！obtainStyledAttributes()有语法提示！！
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        // 根据自定义属性，获取styleable中的src属性
        int key = R.styleable.SkinnableImageView[R.styleable.SkinnableImageView_android_src];
        // 根据styleable获取控件某属性的resourceId
        int backgroundResourceId = attrsBean.getViewResource(key);
        if (backgroundResourceId > 0) {
            // 兼容包转换
            Drawable drawable = ContextCompat.getDrawable(getContext(), backgroundResourceId);
            setImageDrawable(drawable);
        }
    }
}
