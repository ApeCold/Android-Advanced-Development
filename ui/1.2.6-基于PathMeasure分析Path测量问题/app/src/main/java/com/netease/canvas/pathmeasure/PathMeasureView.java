package com.netease.canvas.pathmeasure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.View;

public class PathMeasureView extends View {

    private Paint mPaint = new Paint();
    private Paint mLinePaint = new Paint(); //坐标系
    private Bitmap mBitmap;

    public PathMeasureView(Context context) {
        super(context);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(4);

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStrokeWidth(6);

        //缩小图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow,options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mLinePaint);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mLinePaint);

        canvas.translate(getWidth() / 2, getHeight() / 2);

//        Path path = new Path();
//        path.lineTo(0,200);
//        path.lineTo(200,200);
//        path.lineTo(200,0);
//
//        /**
//         * pathMeasure需要关联一个创建好的path, forceClosed会影响Path的测量结果
//         */
//        PathMeasure pathMeasure = new PathMeasure();
//        pathMeasure.setPath(path, true);
//        Log.e("TAG", "onDraw:forceClosed=true "+ pathMeasure.getLength());
//
//        PathMeasure pathMeasure2 = new PathMeasure();
//        pathMeasure2.setPath(path, false);
//        Log.e("TAG", "onDraw:forceClosed=false "+ pathMeasure2.getLength());
//
//        PathMeasure pathMeasure1 = new PathMeasure(path, false);
//        Log.e("TAG", "onDraw:PathMeasure(path, false) "+ pathMeasure1.getLength());
//
//        path.lineTo(200, -200);
//
//        Log.e("TAG", "onDraw:PathMeasure(path, false) "+ pathMeasure1.getLength());
//        //如果Path进行了调整，需要重新调用setPath方法进行关联
//        pathMeasure1.setPath(path, false);
//
//        Log.e("TAG", "onDraw:PathMeasure(path, false) "+ pathMeasure1.getLength());

//        Path path = new Path();
//        path.addRect(-200,-200, 200,200, Path.Direction.CW);
//
//        Path dst = new Path();
//        dst.lineTo(-300,-300);//添加一条直线
//
//        PathMeasure pathMeasure = new PathMeasure(path, false);
//        //截取一部分存入dst中，并且使用moveTo保持截取得到的Path第一个点位置不变。
//        pathMeasure.getSegment(200, 1000, dst, true);
//
//
//
//        canvas.drawPath(path, mPaint);
//        canvas.drawPath(dst, mLinePaint);

//        Path path = new Path();
//        path.addRect(-100,-100,100,100, Path.Direction.CW);//添加一个矩形
//        path.addOval(-200,-200,200,200, Path.Direction.CW);//添加一个椭圆
//        canvas.drawPath(path, mPaint);
//        PathMeasure pathMeasure = new PathMeasure(path, false);
//        Log.e("TAG", "onDraw:forceClosed=false "+ pathMeasure.getLength());
//        //跳转到下一条曲线
//        pathMeasure.nextContour();
//        Log.e("TAG", "onDraw:forceClosed=false "+ pathMeasure.getLength());

        mPath.reset();
        mPath.addCircle(0,0,200, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);

        mFloat += 0.01;
        if (mFloat >= 1){
            mFloat = 0;
        }

//        PathMeasure pathMeasure = new PathMeasure(mPath, false);
//        pathMeasure.getPosTan(pathMeasure.getLength() * mFloat,pos,tan);
//        Log.e("TAG", "onDraw: pos[0]="+pos[0]+";pos[1]="+pos[1]);
//        Log.e("TAG", "onDraw: tan[0]="+tan[0]+";tan[1]="+tan[1]);
//
//        //计算出当前的切线与x轴夹角的度数
//        double degrees = Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI;
//        Log.e("TAG", "onDraw: degrees="+degrees);
//
//        mMatrix.reset();
//        //进行角度旋转
//        mMatrix.postRotate((float) degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
//        //将图片的绘制点中心与当前点重合
//        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1]-mBitmap.getHeight() / 2);
//        canvas.drawBitmap(mBitmap,mMatrix, mPaint);

        PathMeasure pathMeasure = new PathMeasure(mPath, false);
        //将pos信息和tan信息保存在mMatrix中
        pathMeasure.getMatrix(pathMeasure.getLength() * mFloat, mMatrix, PathMeasure.POSITION_MATRIX_FLAG | PathMeasure.TANGENT_MATRIX_FLAG);
        //将图片的旋转坐标调整到图片中心位置
        mMatrix.preTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);

        canvas.drawBitmap(mBitmap,mMatrix, mPaint);

        invalidate();
    }

    private Matrix mMatrix = new Matrix();
    private float[] pos = new float[2];
    private float[] tan = new float[2];
    private Path mPath = new Path();
    private float mFloat;


}
