package com.netease.butterknife.library;

import android.view.View;

public abstract class DebouncingOnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        doClick(v);
    }

    protected abstract void doClick(View v);
}
