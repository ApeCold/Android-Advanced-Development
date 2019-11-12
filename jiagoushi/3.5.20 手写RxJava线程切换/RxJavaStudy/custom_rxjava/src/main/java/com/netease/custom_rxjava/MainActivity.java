package com.netease.custom_rxjava;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;

// TODO 我们自己写的RxJava
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);

        // Thread.currentThread().getName() == main


        // TODO create 操作符
        /*// 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observableEmitter) { // 使用到了，就产生了读写模式
                Log.d(TAG, "subscribe: 上游开始发射...");
                // 发射事件  可写的
                // todo 使用者去调用发射 2
                observableEmitter.onNext(9); //  <? extends Integer> 不可写了   <? super Integer>可写
                observableEmitter.onComplete();
            }
        })
        // Observable<Integer>.subscribe
        .subscribe(new com.netease.custom_rxjava.Observer<Integer>() { // 下游
            // 接口的实现方法
            @Override
            public void onSubscribe() {
                // todo 1
                Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
            }

            // 接口的实现方法
            @Override
            public void onNext(Integer item) {
                // todo 3
                Log.d(TAG, "下游接收事件 onNext: " + item);
            }

            // 接口的实现方法
            @Override
            public void onError(Throwable e) {

            }

            // 接口的实现方法
            @Override
            public void onComplete() {
                // todo 4 最后一步
                Log.d(TAG, "onComplete: 下游接收事件完成√√√√√√√√√√√√√√");
            }
        });*/



        // --------------------------------------------------------------------------


        // TODO just 操作符
        // 上游
        /*Observable.just("A", "B", "C", "D", "E", "F", "G") // todo 内部执行了第二步
        // 订阅
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe() {
                // todo 1
                Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
            }

            @Override
            public void onNext(String item) {
                // todo 3
                Log.d(TAG, "下游接收事件 onNext: " + item);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                // todo 4
                Log.d(TAG, "onComplete: ");
            }
        });*/


        // --------------------------------------------------------------------------


        // TODO map变换操作符 RxJava的核心
        /*// 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override // todo 6
            public void subscribe(Observer<? super Integer> observableEmitter) { // 使用到了，就产生了读写模式

                // todo observableEmitter == map 包裹了一层

                Log.d(TAG, "subscribe: 上游开始发射...");
                // 发射事件  可写的
                // todo 使用者去调用发射 2
                observableEmitter.onNext(9); //  <? extends Integer> 不可写了   <? super Integer>可写
                observableEmitter.onComplete();
            }
        })

        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                Log.d(TAG, "第1个变换 apply: " + integer);
                return "【" + integer + "】";
            }
        })

        .map(new Function<String, StringBuffer>() {
            @Override
            public StringBuffer apply(String s) {
                Log.d(TAG, "第2个变换 apply: " + s);
                return new StringBuffer().append(s).append("-----------------------");
            }
        })

        .map(new Function<StringBuffer, StringBuffer>() {
            @Override
            public StringBuffer apply(StringBuffer stringBuffer) {
                return new StringBuffer(stringBuffer).append("√√√√√√√√√√√√√√√√√√√√√√√");
            }
        })

        // todo 1
        // ObservableMap<T, R> observableMap = new ObservableMap(source, function);
        // new Observable(observableMap).subscribe
        .subscribe(new com.netease.custom_rxjava.Observer<StringBuffer>() { // 下游
             // 接口的实现方法
             @Override
             public void onSubscribe() {
                 // todo 1
                 Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
             }

             // todo 8
             // 接口的实现方法
             @Override
             public void onNext(StringBuffer item) {
                 // todo 3
                 Log.d(TAG, "下游接收事件 onNext: " + item); // 【9】-----------------------
             }

             // 接口的实现方法
             @Override
             public void onError(Throwable e) {

             }

             // 接口的实现方法
             @Override
             public void onComplete() {
                 // todo 4 最后一步
                 Log.d(TAG, "onComplete: 下游接收事件完成√√√√√√√√√√√√√√");
             }
        });*/

    }

    // TODO // 给同学们新增加的
    public void test(View view) {
        // 给同学们新增加的
        // todo Test fromArray
        String[] strings = {"三分归元气", "风神腿", "排云掌"};
        Observable.fromArray(strings)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe() {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(String item) {
                        Log.d(TAG, "onNext: " + item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e:" + e.getMessage() );
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });


        Log.d(TAG, "test2: -------------------------------------------------");


        String[] strings_1 = {"乔峰", "段誉", "虚竹"};
        String[] strings_2 = {"降龙十八掌", "六脉神剑", "逍遥派武功"};
        String[] strings_3 = {"男主角1", "男主角2", "男主角3"};

        Observable.fromArray(strings_1, strings_2, strings_3)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe() {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(String item) {
                        Log.d(TAG, "onNext: " + item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e:" + e.getMessage() );
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }



    // todo 打印 上游 下游 线程
    public void test2(View view) {
        // 最顶端
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observableEmitter) {
                Log.d(TAG, "上游 subscribe: --- " + Thread.currentThread().getName());
                observableEmitter.onNext(1);
            }
        })
        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                Log.d(TAG, "map 1  --- " + Thread.currentThread().getName());
                return integer + ">>>";
            }
        })
        .map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                Log.d(TAG, "map 2  --- " + Thread.currentThread().getName());
                return s + "???";
            }
        })

        // 给上游分配异步线程
        .observables_On()

        // 给下游分配Android主线程
        // new Observable<T>(下游分配异步线程 == ObservableOnIO).xxx
        .observers_AndroidMain_On()

        // main线程.订阅
        // new Observable<T>(下游分配Android主线程 == ObserverAndroidMain_On).订阅
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe() {

            }

            @Override
            public void onNext(String item) {
                Log.d(TAG, "下游 onNext: --- " + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    private ProgressDialog progressDialog;

    // todo 用我们自己写的RxJava下载图片  异步下载 线程切换 更新UI
    public void test3(View view) {

        final String PATH = "http://img.redocn.com/sheying/20140731/qinghaihuyuanjing_2820969.jpg";

        Observable.just(PATH)
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) {
                        try {
                            Thread.sleep(2000);
                            URL url = new URL(PATH);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setConnectTimeout(5000);
                            int code = httpURLConnection.getResponseCode();
                            if (code == HttpURLConnection.HTTP_OK) {
                                InputStream inputStream = httpURLConnection.getInputStream();
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                return bitmap;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            // 关闭流  系统资源
                        }
                        return null;
                    }
                })

                // 给图片加水印
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) {
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setTextSize(80);
                        return drawTextToBitmap(bitmap, "RxJava直播课 手写线程切换", paint, 80, 80);
                    }
                })

                // 给上游分配异步线程 下载图片
                .observables_On()

                // 给下游分配主线程 更新UI
                .observers_AndroidMain_On()

                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("正在下载中...");
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(Bitmap item) {
                        imageView.setImageBitmap(item);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                });

    }


    //图片上绘制文字
    private Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    public static void main(String[] args) {
        System.out.println(1);

        // 泛型擦除
        // Test<Boolean> test = new Test<>();
        Test.add(false);
        show(new <Boolean>Test()); // --- class 虚拟机执行
    }

    // Person  or  Person的子类
    /*private static void show(Test<? extends Person> test) {

    }*/

    // Person  or  Person的父类
    private static void show(Test<? super Object> test) {

    }

    static class Test<T> {

        public static void add(Boolean b){

        }

    }

    class Person {

    }

    class Student extends Person {

    }

    class Worker extends Person {

    }

}
