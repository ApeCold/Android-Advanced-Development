package com.wangyi.palyerriip;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
//    入口   绘制 Path
//组合控件  组合   播放界面
//背景单独组合
//  唱针   唱盘组合
//底盘来适配

//业务
//
//事件控件  事件动画     临界值   QQ  拖着   临界值
//高级UI  播放
// NDK  网易云信   直播 安全  视频

public class PathLoadingView extends View {
//    PathMeasure
    private Path mPath;
    private Paint mPaint;
    private float mLength;
    private Path dst;
    private float mAnimatorValue;

//    新加入的
    private PathMeasure mPathMeasure;
    public PathLoadingView(Context context) {
        super(context);
    }


    public PathLoadingView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        //画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#FF4081"));
        mPaint.setStrokeWidth(10f);
        mPaint.setStyle(Paint.Style.STROKE);
        //Path
        mPath = new Path();
        mPath.addCircle(300f, 300f, 100f, Path.Direction.CW);//加入一个半径为100圆

// 闭合  圆     直线 非闭合  0  --length
//        pathmeasure  path的测量工具类
        mPathMeasure = new PathMeasure(mPath,true);
        mLength=mPathMeasure.getLength();//不需要你自己算  //
//        0<distance<length*百分比
        dst = new Path();
        //属性动画
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        //设置动画过程的监听
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        valueAnimator.start();
    }

    public PathLoadingView(Context context,   AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dst.reset();
        float distance = mLength * mAnimatorValue;
//        distance     start  disant -0
        float start = (float) (distance - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * mLength));
//   ( distance-0.5*mLength)   start   distance
//mPath  1  dst  2
        mPathMeasure.getSegment(start, distance, dst, true);
        canvas.drawPath(dst,mPaint);
    }
}
