package com.netease.gifcompare;

import android.graphics.Bitmap;

public class GifNdkDecoder {
    private long gifPointer;

    static {
        System.loadLibrary("native-lib");
    }

    public static native int getWidth(long gifPointer);

    public static native int getHeight(long gifPointer);

    public static native long loadGif(String path);

    public static native int updateFrame(Bitmap bitmap, long gifPointer);

    public GifNdkDecoder(long gifPoint) {
        this.gifPointer = gifPoint;
    }

    public static GifNdkDecoder load(String path) {
        long gifHander = loadGif(path);
        GifNdkDecoder gifHandler = new GifNdkDecoder(gifHander);
        return gifHandler;
    }

    public long getGifPointer() {
        return gifPointer;
    }
}
