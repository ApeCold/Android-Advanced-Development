package com.netease.butterknife.demo;

import android.view.View;

import com.netease.butterknife.library.DebouncingOnClickListener;
import com.netease.butterknife.library.ViewBinder;

public class MainActivity_ViewBinder implements ViewBinder<MainActivity> {

    @Override
    public void bind(final MainActivity target) {
        target.tv = target.findViewById(R.id.tv);
        target.findViewById(R.id.tv).setOnClickListener(new DebouncingOnClickListener() {

            @Override
            public void doClick(View v) {
                target.click(v);
            }
        });
    }
}
