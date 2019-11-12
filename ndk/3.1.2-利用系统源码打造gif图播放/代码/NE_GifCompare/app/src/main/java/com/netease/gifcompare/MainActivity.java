package com.netease.gifcompare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageView      image;
    private GifTask        mGifTask;
    private Bitmap         bitmap;
    private GifNdkDecoder  gifNdkDecoder;
    private GifJavaDecoder gifJavaDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.image);
    }

    public void javaLoadGif(View view) {
        //对Gif图片进行解码
        InputStream is = null;
        try {
            is = new FileInputStream(
                    new File(Environment.getExternalStorageDirectory(), "demo.gif"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        gifJavaDecoder = new GifJavaDecoder();
        int ret = gifJavaDecoder.read(is);
        if (ret == GifJavaDecoder.STATUS_OK) {
            Log.e(TAG, "gif文件读取成功！");
            mGifTask = new GifTask(image, gifJavaDecoder.getFrames());
            mGifTask.startTask();
            new Thread(mGifTask).start();
        } else if (ret == GifJavaDecoder.STATUS_FORMAT_ERROR) {
            Log.e(TAG, "gif文件格式错误！");
        } else {
            Log.e(TAG, "gif文件读取失败！请检查文件是否存在或确认是否添加sd卡读写权限！");
        }
    }

    //用来循环播放Gif每帧图片
    private class GifTask implements Runnable {
        int                       i                      = 0;
        ImageView                 iv;
        GifJavaDecoder.GifFrame[] frames;
        int                       framelen, oncePlayTime = 0;

        public GifTask(ImageView iv, GifJavaDecoder.GifFrame[] frames) {
            this.iv = iv;
            this.frames = frames;

            int n = 0;
            framelen = frames.length;
            while (n < framelen) {
                oncePlayTime += frames[n].delay;
                n++;
            }
            Log.d(TAG, "playTime= " + oncePlayTime);
        }

        Handler h2 = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        iv.setImageBitmap((Bitmap) msg.obj);
                        break;
                }
            }
        };

        @Override
        public void run() {
            if (!frames[i].image.isRecycled()) {
                //      iv.setImageBitmap(frames[i].image);
                Message m = Message.obtain(h2, 1, frames[i].image);
                m.sendToTarget();
            }
            iv.postDelayed(this, frames[i++].delay);
            i %= framelen;
        }

        public void startTask() {
            iv.post(this);
        }

        public void stopTask() {
            if (null != iv) iv.removeCallbacks(this);
            iv = null;
            if (null != frames) {
                for (GifJavaDecoder.GifFrame frame : frames) {
                    if (frame.image != null && !frame.image.isRecycled()) {
                        frame.image.recycle();
                        frame.image = null;
                    }
                }
                frames = null;
            }
        }
    }

    public void ndkLoadGif(View view) {
//        File file = new File(Environment.getExternalStorageDirectory(), "demo2.gif");
//        long start = System.currentTimeMillis();
//        gifNdkDecoder = GifNdkDecoder.load(file.getAbsolutePath());
//        Log.e(TAG, "load gif 耗时" + (System.currentTimeMillis() - start));
//        int width = gifNdkDecoder.getWidth(gifNdkDecoder.getGifPointer());
//        int height = gifNdkDecoder.getHeight(gifNdkDecoder.getGifPointer());
//        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        long mNextFrameRenderTime =
//                gifNdkDecoder.updateFrame(bitmap, gifNdkDecoder.getGifPointer());
//        myHandler.sendEmptyMessageDelayed(1, mNextFrameRenderTime);

        new MyAsyncTask().execute();
    }

    class MyAsyncTask extends AsyncTask<Void,Void,Void>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("正在加载Gif图片...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File file = new File(Environment.getExternalStorageDirectory(), "demo.gif");
            long start = System.currentTimeMillis();
            gifNdkDecoder = GifNdkDecoder.load(file.getAbsolutePath());
            Log.e(TAG, "load gif 耗时" + (System.currentTimeMillis() - start));
            int width = gifNdkDecoder.getWidth(gifNdkDecoder.getGifPointer());
            int height = gifNdkDecoder.getHeight(gifNdkDecoder.getGifPointer());
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            long mNextFrameRenderTime =
                    gifNdkDecoder.updateFrame(bitmap, gifNdkDecoder.getGifPointer());
            myHandler.sendEmptyMessageDelayed(1, mNextFrameRenderTime);
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            long mNextFrameRenderTime =
                    gifNdkDecoder.updateFrame(bitmap, gifNdkDecoder.getGifPointer());
            myHandler.sendEmptyMessageDelayed(1, mNextFrameRenderTime);
            image.setImageBitmap(bitmap);
        }
    };
}
