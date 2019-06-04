package com.netease.paint.xfermode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class XfermodesView extends View {


    private static int W = 250;
    private static int H = 250;

    private static final int ROW_MAX = 4;   // number of samples per row

    private Bitmap mSrcB;
    private Bitmap mDstB;
    private Shader mBG;     // background checker-board pattern

    //其中Sa全称为Source alpha表示源图的Alpha通道；Sc全称为Source color表示源图的颜色；Da全称为Destination alpha表示目标图的Alpha通道；Dc全称为Destination color表示目标图的颜色，[...,..]前半部分计算的是结果图像的Alpha通道值，“,”后半部分计算的是结果图像的颜色值。
    //效果作用于src源图像区域
    private static final Xfermode[] sModes = {
            //所绘制不会提交到画布上
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            //显示上层绘制的图像
            new PorterDuffXfermode(PorterDuff.Mode.SRC),
            //显示下层绘制图像
            new PorterDuffXfermode(PorterDuff.Mode.DST),
            //正常绘制显示，上下层绘制叠盖
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),

            //上下层都显示，下层居上显示
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            //取两层绘制交集，显示上层
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            //取两层绘制交集，显示下层
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            //取上层绘制非交集部分，交集部分变成透明
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),

            //取下层绘制非交集部分，交集部分变成透明
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            //取上层交集部分与下层非交集部分
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            //取下层交集部分与上层非交集部分
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            //去除两图层交集部分
            new PorterDuffXfermode(PorterDuff.Mode.XOR),

            //取两图层全部区域，交集部分颜色加深
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            //取两图层全部区域，交集部分颜色点亮
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            //取两图层交集部分，颜色叠加
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            //取两图层全部区域，交集部分滤色
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN),

            //取两图层全部区域，交集部分饱和度相加
            new PorterDuffXfermode(PorterDuff.Mode.ADD),
            //取两图层全部区域，交集部分叠加
            new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
    };

    private static final String[] sLabels = {
            "Clear", "Src", "Dst", "SrcOver",
            "DstOver", "SrcIn", "DstIn", "SrcOut",
            "DstOut", "SrcATop", "DstATop", "Xor",
            "Darken", "Lighten", "Multiply", "Screen", "Add","Overlay"
    };

    public XfermodesView(Context context) {
        this(context, null);
    }

    public XfermodesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XfermodesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics display = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(display);
            W = H = (display.widthPixels - 64) / ROW_MAX; //得到矩形
        }

        //1，API 14之后，有些函数不支持硬件加速，需要禁用
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mSrcB = makeSrc(W, H);
        mDstB = makeDst(W, H);

        //根据width和height创建空位图，然后用指定的颜色数组colors来从左到右从上至下一次填充颜色
        //make a ckeckerboard pattern
        Bitmap bm = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFFCCCCCC, 0xFFCCCCCC, 0xFFFFFFFF}, 2, 2, Bitmap.Config.RGB_565);
        mBG = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Matrix m = new Matrix();
        m.setScale(6, 6);
        mBG.setLocalMatrix(m);
    }

//    //google api的画法
//    @Override
//    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.WHITE);
//
//        Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
//        labelP.setTextAlign(Paint.Align.CENTER);
//
//        Paint paint = new Paint();
//        //设置是否使用双线性过滤来绘制Bitmap，图像在放大绘制的时候，默认使用的是最近邻插值过滤，这种算法简单，但会出现马赛克现象；而如果开启了双线性过滤，就可以让结果图像显得更加平滑
//        paint.setFilterBitmap(false);
//
//        canvas.translate(15, 35);
//
//        int x = 0;
//        int y = 0;
//        for (int i = 0; i < sModes.length; i++) {
//            // draw the border
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setShader(null);
//            canvas.drawRect(x - 0.5f, y - 0.5f, x + W + 0.5f, y + H + 0.5f, paint);
//
//            // draw the checker-board pattern
//            paint.setStyle(Paint.Style.FILL);
//            paint.setShader(mBG);
//            canvas.drawRect(x, y, x + W, y + H, paint);
//
//            // draw the src/dst example into our offscreen bitmap
//            int sc = canvas.saveLayer(x, y, x + W, y + H, null);
//            canvas.translate(x, y);
//            canvas.drawBitmap(mDstB, 0, 0, paint);
//            paint.setXfermode(sModes[i]);
//            canvas.drawBitmap(mSrcB, 0, 0, paint);
//            paint.setXfermode(null);
//            canvas.restoreToCount(sc);
//
//            // draw the label
//            labelP.setTextSize(20);
//            canvas.drawText(sLabels[i], x + W / 2, y - labelP.getTextSize() / 2, labelP);
//
//            x += W + 10;
//
//            // wrap around when we've drawn enough for one row
//            if ((i % ROW_MAX) == ROW_MAX - 1) {
//                x = 0;
//                y += H + 30;
//            }
//        }
//    }
//
//    // create a bitmap with a circle, used for the "dst" image
//    static Bitmap makeDst(int w, int h) {
//        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bm);
//        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//        p.setColor(0xFFFFCC44);
//        c.drawOval(new RectF(0, 0, w * 3 / 4, h * 3 / 4), p);
//        return bm;
//    }
//
//    // create a bitmap with a rect, used for the "src" image
//    static Bitmap makeSrc(int w, int h) {
//        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bm);
//        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//        p.setColor(0xFF66AAFF);
//        c.drawRect(w / 3, h / 3, w * 19 / 20, h * 19 / 20, p);
//        return bm;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelP.setTextAlign(Paint.Align.CENTER);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);

        canvas.translate(15, 35);

        int x = 0;
        int y = 0;
        for (int i = 0; i < sModes.length; i++) {
            // draw the border
            paint.setStyle(Paint.Style.STROKE);
            paint.setShader(null);
            canvas.drawRect(x - 0.5f, y - 0.5f, x + W + 0.5f, y + H + 0.5f, paint);

            // draw the checker-board pattern
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(mBG);
            canvas.drawRect(x, y, x + W, y + H, paint);

            // 使用离屏绘制
            int sc = canvas.saveLayer(x, y, x + W, y + H, null);
            canvas.translate(x, y);
            canvas.drawBitmap(makeDst(2 * W / 3, 2 * H / 3), 0, 0, paint);
            paint.setXfermode(sModes[i]);
            canvas.drawBitmap(makeSrc(2 * W / 3, 2 * H / 3), W / 3, H / 3, paint);
            paint.setXfermode(null);
            canvas.restoreToCount(sc);

            // draw the label
            labelP.setTextSize(20);
            canvas.drawText(sLabels[i], x + W / 2, y - labelP.getTextSize() / 2, labelP);

            x += W + 10;

            // wrap around when we've drawn enough for one row
            if ((i % ROW_MAX) == ROW_MAX - 1) {
                x = 0;
                y += H + 30;
            }
        }
    }

    // create a bitmap with a circle, used for the "dst" image
    // 画圆一个完成的圆
    static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(0, 0, w, h), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    // 矩形右下角留有透明间隙
    static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF66AAFF);
        c.drawRect(0, 0, w * 19 / 20, h * 19 / 20, p);
        return bm;
    }


}
