package com.netease.canvas.path;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * 多介贝塞尔曲线
 */
public class BezierView extends View {

    private Paint mPaint, mLinePointPaint;
    private Path mPath;

    //多控制点
    private ArrayList<PointF> mControlPoints;    //控制点集,没有分数据点还是控制点

    public BezierView(Context context) {
        this(context, null);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);

        mLinePointPaint = new Paint();
        mLinePointPaint.setAntiAlias(true);
        mLinePointPaint.setStrokeWidth(4);
        mLinePointPaint.setStyle(Paint.Style.STROKE);
        mLinePointPaint.setColor(Color.GRAY);

        mPath = new Path();
        mControlPoints = new ArrayList<>();
        init();
    }

    private void init() {
        mControlPoints.clear();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            int x = random.nextInt(800) + 200;
            int y = random.nextInt(800) + 200;
            PointF pointF = new PointF(x, y);
            mControlPoints.add(pointF);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 控制点和控制点连线
        int size = mControlPoints.size();
        PointF point;
        for (int i = 0; i < size; i++) {
            point = mControlPoints.get(i);
            if (i > 0) {
                // 控制点连线
                canvas.drawLine(mControlPoints.get(i - 1).x, mControlPoints.get(i - 1).y, point.x, point.y, mLinePointPaint);
            }
            // 控制点
            canvas.drawCircle(point.x, point.y, 12, mLinePointPaint);
        }

        buildBezierPoints();
        canvas.drawPath(mPath, mPaint);
    }


    /**
     * deCasteljau算法
     * p(i,j) =  (1-t) * p(i-1,j)  +  t * p(i-1,j+1);
     *
     * @param i 阶数   4
     * @param j 控制点 3
     * @param t 时间
     * @return
     */
    private float deCasteljauX(int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * mControlPoints.get(j).x + t * mControlPoints.get(j + 1).x;
        }
        return (1 - t) * deCasteljauX(i - 1, j, t) + t * deCasteljauX(i - 1, j + 1, t);
    }

    /**
     * deCasteljau算法
     *
     * @param i 阶数
     * @param j 第几个点
     * @param t 时间
     * @return
     */
    private float deCasteljauY(int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * mControlPoints.get(j).y + t * mControlPoints.get(j + 1).y;
        }
        return (1 - t) * deCasteljauY(i - 1, j, t) + t * deCasteljauY(i - 1, j + 1, t);
    }


    private ArrayList<PointF> buildBezierPoints() {
        mPath.reset();

        ArrayList<PointF> points = new ArrayList<>();
        int order = mControlPoints.size() - 1; //阶数
        //画的密集度，帧
        float delta = 1.0f / 1000;
        for (float t = 0; t <= 1; t += delta) {
            // Bezier点集
            PointF pointF = new PointF(deCasteljauX(order, 0, t), deCasteljauY(order, 0, t));
            points.add(pointF);
            if (points.size() == 1) {
                mPath.moveTo(points.get(0).x, points.get(0).y);
            } else {
                mPath.lineTo(pointF.x, pointF.y);
            }

        }
        return points;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            init();
            invalidate();
        }
        return true;
    }
}
