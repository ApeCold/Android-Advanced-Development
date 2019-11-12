package com.netease.rxjavastudy.retrofit_okhttp_rxjava;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.netease.rxjavastudy.R;
import com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp.IRequestNetwork;
import com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp.LoginRequest;
import com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp.LoginResponse;
import com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp.MyRetrofit;
import com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp.RegisterRequest;
import com.netease.rxjavastudy.retrofit_okhttp_rxjava.retrofit_okhttp.RegisterResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Retrofit + RxJava
 * 需求：
 * 1.请求服务器注册操作
 * 2.注册完成之后，更新注册UI
 * 3.马上去登录服务器操作
 * 4.登录完成之后，更新登录的UI
 */
public class TestActivity extends AppCompatActivity {

    private final String TAG = TestActivity.class.getSimpleName();

    private TextView tv_register_ui;
    private TextView tv_login_ui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        tv_register_ui = findViewById(R.id.tv_login_ui);
        tv_login_ui = findViewById(R.id.tv_login_ui);
    }

    // 方法1
    public void request(View view) {
        // 分开写
        /**
         * 1.请求服务器注册操作
         * 2.注册完成之后，更新注册UI
         */
        // IRequestNetwork iRequestNetwork = MyRetrofit.createRetrofit().create(IRequestNetwork.class);

        // 1.请求服务器注册操作
        MyRetrofit.createRetrofit().create(IRequestNetwork.class) // IRequestNetwork
                // IRequestNetwork.registerAction
                .registerAction(new RegisterRequest())  // Observable<RegisterResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io()) // todo 给上游分配异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游切换 主线程
                 // 2.注册完成之后，更新注册UI
                 .subscribe(new Consumer<RegisterResponse>() { // 下游 简化版
                     @Override
                     public void accept(RegisterResponse registerResponse) throws Exception {
                         // 更新注册相关的所有UI
                         // .....
                     }
                 });





        // 3.马上去登录服务器操作
        MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                .loginAction(new LoginRequest())  // Observable<LoginResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io()) // todo 给上游分配异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游切换 主线程

                // 4.登录完成之后，更新登录的UI
                .subscribe(new Consumer<LoginResponse>() { // 下游 简化版
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        // 更新登录相关的所有UI
                        // .....
                    }
                });
    }

    private ProgressDialog progressDialog;

    public void request2(View view) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在执行中...");

        /**
         * 一行代码 实现需求
         * 需求：
         *  * 1.请求服务器注册操作
         *  * 2.注册完成之后，更新注册UI
         *  * 3.马上去登录服务器操作
         *  * 4.登录完成之后，更新登录的UI
         */
        MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                //  1.请求服务器注册操作  // todo 第二步 请求服务器 注册操作
                .registerAction(new RegisterRequest()) // Observable<RegisterResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io()) // todo 给上游分配异步线程

                .observeOn(AndroidSchedulers.mainThread()) // todo 给下游切换 主线程
                // 2.注册完成之后，更新注册UI

                /**
                 *  这样不能订阅，如果订阅了，就无法执行
                 *      3 马上去登录服务器操作
                 *      4.登录完成之后，更新登录的UI
                 *
                 *  所以我们要去学习一个 .doOnNext()，可以在不订阅的情况下，更新UI
                 */
                .doOnNext(new Consumer<RegisterResponse>() { // 简单版本的下游
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        // todo 第三步 更新注册相关的所有UI
                        // 更新注册相关的所有UI
                        tv_register_ui.setText("xxx");
                        // .......
                    }
                })
                // 3.马上去登录服务器操作 -- 耗时操作
                .subscribeOn(Schedulers.io()) // todo 分配异步线程
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        // 还可以拿到 注册后的响应对象RegisterResponse
                        // 执行耗时操作
                        // 马上去登录服务器操作 -- 耗时操作
                        Observable<LoginResponse> observable = MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                                .loginAction(new LoginRequest());  // todo 第四步 马上去登录服务器操作 -- 耗时操作
                        return observable;
                    }
                })
                // 4.登录完成之后，更新登录的UI
                .observeOn(AndroidSchedulers.mainThread()) // // todo 给下游切换 主线程
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // todo 第一步
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        // 更新登录相关的所有UI
                        // todo 第五步 更新登录相关的所有UI
                        tv_login_ui.setText("xxxx");
                        // ...........
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // todo 第六步
                        progressDialog.dismiss(); // 结束对话框 ，整个流程完成
                    }
                });
    }
}
