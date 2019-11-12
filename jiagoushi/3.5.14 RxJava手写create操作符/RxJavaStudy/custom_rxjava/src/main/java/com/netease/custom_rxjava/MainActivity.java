package com.netease.custom_rxjava;

import android.arch.lifecycle.Observer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// TODO 我们自己写的RxJava
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(Observer<Integer> observableEmitter) {

            }
        })
        // Observable<Integer>.subscribe
        .subscribe(new com.netease.custom_rxjava.Observer<Integer>() { // 下游
            @Override
            public void onSubscribe() {

            }

            @Override
            public void onNext(Integer item) {

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
