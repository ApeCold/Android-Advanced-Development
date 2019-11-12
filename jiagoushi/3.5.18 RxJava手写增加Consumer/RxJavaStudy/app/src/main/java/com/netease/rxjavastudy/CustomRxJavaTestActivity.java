package com.netease.rxjavastudy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class CustomRxJavaTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).subscribe(new Observer<Integer>() { // 下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        /// ---------------------- TODO 使用map
        // 上游 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onComplete();
            }
        })

        // 功能一
        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return null;
            }
        })

        // 功能二
        .map(new Function<String, Bitmap>() {
            @Override
            public Bitmap apply(String s) throws Exception {
                return null;
            }
        })

        // 功能三
        .map(new Function<Bitmap, InputStream>() {
            @Override
            public InputStream apply(Bitmap bitmap) throws Exception {
                return null;
            }
        })

        // ...

         // 订阅
        .subscribe(new Observer<InputStream>() { // 下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(InputStream integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
