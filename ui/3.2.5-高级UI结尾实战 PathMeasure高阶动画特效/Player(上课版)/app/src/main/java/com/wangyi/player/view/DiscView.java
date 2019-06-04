package com.wangyi.player.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wangyi.player.R;
import com.wangyi.player.ViewPagerAdapter;
import com.wangyi.player.ui.UIUtils;
import com.wangyi.player.ui.ViewCalculateUtil;
import com.wangyi.player.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class DiscView   extends RelativeLayout {
    //    Pager的View
    private List<View> mDiscLayouts = new ArrayList<>();

    private List<Integer> mMusicDatas = new ArrayList<>();

    private List<ObjectAnimator> mDiscAnimators = new ArrayList<>();
    ImageView musicNeedle;
    ImageView musicCircle;
    private ViewPagerAdapter mViewPagerAdapter;
    private ObjectAnimator mNeedleAnimator;
    private ViewPager viewPager;

    private MusicLitener musicLitener;

    public void setMusicLitener(MusicLitener musicLitener) {
        this.musicLitener = musicLitener;
    }

    public DiscView(Context context) {
        super(context);
    }

    public DiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    //    歌曲列表
    public void setMusicDataList(List<Integer> musicDataList) {

        if (musicDataList.isEmpty()) return;
        mDiscLayouts.clear();
        mMusicDatas.clear();
        mDiscAnimators.clear();
        mMusicDatas.addAll(musicDataList);
        for (Integer resd : mMusicDatas) {
            View centerCotainer = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc,
                    viewPager, false);
            ImageView centerImage = (ImageView) centerCotainer.findViewById(R.id.music_img);
            Drawable drawable = BitmapUtil.getMusicItemDrawable(getContext(), resd);
            ViewCalculateUtil.setViewLinearLayoutParam(centerImage, 800, 800, ((1000 - 800) / 2)+190, 0, 0, 0);
            centerImage.setImageDrawable(drawable);

            mDiscLayouts.add(centerCotainer);
//            准备   指针动画执行之后   唱盘
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(centerImage, View.ROTATION, 0, 360);
            rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnimator.setDuration(20 * 1000);
            rotateAnimator.setInterpolator(new LinearInterpolator());
            mDiscAnimators.add(rotateAnimator);


        }
        mViewPagerAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        initObjectAnimator();
        mViewPagerAdapter = new ViewPagerAdapter(mDiscLayouts);
        viewPager.setAdapter(mViewPagerAdapter);
    }

    private void initObjectAnimator() {
        mNeedleAnimator = ObjectAnimator.ofFloat(musicNeedle, View.ROTATION,-30, 0);
        mNeedleAnimator.setDuration(500);
        mNeedleAnimator.setInterpolator(new AccelerateInterpolator());
        mNeedleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int index = viewPager.getCurrentItem();
//                开启唱盘的动画
                playDiscAnimator(index);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void playDiscAnimator(int index) {
        ObjectAnimator objectAnimator = mDiscAnimators.get(index);
        if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        }else {
            objectAnimator.start();
        }
        if (musicLitener != null) {
            musicLitener.onMusicPicChanged(mMusicDatas.get(viewPager.getCurrentItem()));

        }

    }

    private void initView() {
//        唱针
        musicNeedle = findViewById(R.id.musicNeedle);
        viewPager = findViewById(R.id.viewPager);
//        底盘
        musicCircle = findViewById(R.id.musicCircle);
        int musicCircleWidth=UIUtils.getInstance().getWidth(813);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc_blackground), musicCircleWidth, musicCircleWidth, false);

        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapDisc);
        musicCircle.setImageDrawable(roundDiscDrawable);
        ViewCalculateUtil.setViewLayoutParam(musicCircle, 1000, 1000, 190, 0, 0, 0);
//事件    ---》难处理
//        设置指针
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
                .ic_needle);
        Bitmap bitmap = Bitmap.createScaledBitmap(originBitmap, UIUtils.getInstance().getWidth(276), UIUtils.getInstance().getWidth(276), false);

        ViewCalculateUtil.setViewLayoutParam(musicNeedle, 276, 413, 43, 0, 500, 0);

        musicNeedle.setPivotX(UIUtils.getInstance().getWidth(43));
        musicNeedle.setPivotY(UIUtils.getInstance().getHeight(43));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentItem = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {




            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }
//状态发生改变      viewpage
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
//                    0：什么都没做
                    case ViewPager.SCROLL_STATE_IDLE:
                        break;
//                        2：滑动结束
                    case ViewPager.SCROLL_STATE_SETTLING: {

                        playAnimator();

                        break;
                    }
//                    1：开始滑动
                    case ViewPager.SCROLL_STATE_DRAGGING: {
                        pauseAnimator();
                        break;
                    }
                }
            }
        });


    }

    private void pauseAnimator() {
//        唱盘动画     重置 唱针动画
        ObjectAnimator objectAnimator = mDiscAnimators.get(viewPager.getCurrentItem());
        objectAnimator.pause();
        mNeedleAnimator.reverse();
    }

    private void playAnimator() {
        mNeedleAnimator.start();
    }
}