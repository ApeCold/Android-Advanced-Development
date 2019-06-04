package com.wangyi.splashwangyi;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class ParallaxContainer extends FrameLayout implements ViewPager.OnPageChangeListener {
    private List<ParallaxFragment> fragments;
    private ParallaxPagerAdapter adapter;
    public ParallaxContainer(@NonNull Context context) {
        super(context);
    }
    private ImageView iv_man;
    public void setIv_man(ImageView iv_man) {
        this.iv_man = iv_man;
    }

    public ParallaxContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void setUp(int... childIds){
//        Fragment数组
        fragments = new ArrayList<ParallaxFragment>();
        for (int i = 0; i < childIds.length; i++) {
            ParallaxFragment f = new ParallaxFragment();
//
            Bundle args = new Bundle();
            //Fragment中需要加载的布局文件id
            args.putInt("layoutId", childIds[i]);
            f.setArguments(args);
            fragments.add(f);
        }

        ViewPager vp = new ViewPager(getContext());
        vp.setId(R.id.parallax_pager);
        //实例化适配器
        SplashActivity activity = (SplashActivity)getContext();
        adapter=new ParallaxPagerAdapter(activity.getSupportFragmentManager(), fragments);
        vp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(this);
        addView(vp,0);
    }









    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        动画
        int containerWidth = getWidth();
        ParallaxFragment outFragment = null;

        try {
            outFragment = fragments.get(position - 1);
        } catch (Exception e) {}
        //获取到退出的页面
        ParallaxFragment inFragment = null;
        try {
            inFragment = fragments.get(position);
        } catch (Exception e) {}

        if (outFragment != null) {
            //获取Fragment上所有的视图，实现动画效果
            List<View> inViews = outFragment.getParallaxViews();
//            动画
            if (inViews != null) {
                for (View view : inViews) {
//
                    ParallaxViewTag tag = (ParallaxViewTag) view.getTag(R.id.parallax_view_tag);
                    if (tag == null) {
                        continue;
                    }
                    ViewHelper.setTranslationX(view, (containerWidth - positionOffsetPixels) * tag.xIn);
                    ViewHelper.setTranslationY(view, (containerWidth - positionOffsetPixels) * tag.yIn);
                }

            }

        }
       	if(inFragment != null){
            List<View> outViews = inFragment.getParallaxViews();
            if (outViews != null) {
                for (View view : outViews) {
                    ParallaxViewTag tag = (ParallaxViewTag) view.getTag(R.id.parallax_view_tag);
                    if (tag == null) {
                        continue;
                    }
                    //仔细观察退出的fragment中view从原始位置开始向上移动，translationY应为负数
                    ViewHelper.setTranslationY(view, 0 - positionOffsetPixels * tag.yOut);
                    ViewHelper.setTranslationX(view, 0 - positionOffsetPixels * tag.xOut);
                }
            }
        }
    }





    @Override
    public void onPageSelected(int position) {

        if (position == adapter.getCount() - 1) {
            iv_man.setVisibility(INVISIBLE);
        }else{
            iv_man.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state){
        AnimationDrawable animation = (AnimationDrawable) iv_man.getBackground();
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                animation.start();
                break;

            case ViewPager.SCROLL_STATE_IDLE:
                animation.stop();
                break;

            default:
                break;
        }
    }
}
