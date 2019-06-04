package com.wangyi.player.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.wangyi.player.R;
import com.wangyi.player.ui.UIUtils;

public class BitmapUtil {

    public static Drawable getMusicItemDrawable(Context context,int resId) {
        int outPicSize = UIUtils.getInstance().getWidth(813);
        int intPicSize = UIUtils.getInstance().getWidth(533);
//        外面的圆盘
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R
                .drawable.ic_disc), outPicSize, outPicSize, false);




        //        里面的图片    ---》正方形
        Bitmap bitmapMusicPic = getMusicPicBitmap(context,intPicSize,resId);
        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                (context.getResources(), bitmapMusicPic);

        //抗锯齿
        discDrawable.setAntiAlias(true);
        roundMusicDrawable.setAntiAlias(true);


        Drawable[] drawables = new Drawable[2];
        drawables[0] = roundMusicDrawable;
        drawables[1] = discDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int musicPicMargin = (int) ((outPicSize-intPicSize)/2);
        //调整专辑图片的四周边距，让其显示在正中
        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
                musicPicMargin);
        return layerDrawable;
    }


    public static Bitmap getMusicPicBitmap(Context context,int musicPicSize, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(),resId,options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //设置图片采样率
        options.inSampleSize = dstSample;
        //设置图片解码格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                resId, options), musicPicSize, musicPicSize, true);
    }


}
