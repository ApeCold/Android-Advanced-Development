package com.netease.dagger2customproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.netease.custom_dagger2.Provider;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Inject
    Work work;
    // MainActivity_MembersInjector

    @Inject
    Work work2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerWorkComponent.create().inject(this);

        work.setValue("liu");

        Log.d(TAG, "onCreate: work:" + work.hashCode() + "  --- " + work2.hashCode());
    }
}
