package com.wangyi.wangyiview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

public class CarView extends View {
    private Bitmap carBitmap;

    private Path path;
    private PathMeasure pathMeasure; //路径计算
    private float distanceRatio = 0;
    private Paint circlePaint; //画圆圈的画笔
    private Paint carPaint; //画小车的画笔
    private Matrix carMatrix; //针对car bitmap图片操作的矩阵
    public CarView(Context context) {
        super(context);
        init();
    }

    private void init() {
        carBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_car);
        path = new Path();
        path.addCircle(0, 0, 200, Path.Direction.CW);
        pathMeasure = new PathMeasure(path, false);
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);

        carPaint = new Paint();
        carPaint.setColor(Color.DKGRAY);
        carPaint.setStrokeWidth(2);
        carPaint.setStyle(Paint.Style.STROKE);

        carMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //移动canvas坐标系到中心
        canvas.translate(width / 2, height / 2);
        carMatrix.reset();
        distanceRatio +=  0.006f;
        if(distanceRatio >=1){
            distanceRatio = 0;
        }
//        0 - 1
        float[] pos = new float[2]; //记录位置
        float[] tan = new float[2]; //记录切点值xy
        float distance = pathMeasure.getLength() * distanceRatio;
        pathMeasure.getPosTan(distance, pos, tan);
//        tan0代表 cos tan[1] sin
        float degree = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI); //计算小车本身要旋转的角度
        carMatrix.postRotate(degree, carBitmap.getWidth() / 2, carBitmap.getHeight() / 2); //设置旋转角度和旋转中心
        //这里要将设置到小车的中心点
        carMatrix.postTranslate(pos[0] - carBitmap.getWidth() / 2, pos[1] - carBitmap.getHeight() / 2);
        canvas.drawPath(path, circlePaint);
        canvas.drawBitmap(carBitmap, carMatrix, carPaint);
        invalidate();
    }

    public CarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
