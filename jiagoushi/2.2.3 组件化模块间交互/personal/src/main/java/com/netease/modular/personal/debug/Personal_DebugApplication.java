package com.netease.modular.personal.debug;

import android.app.Application;
import android.util.Log;

import com.netease.common.utils.Cons;

public class Personal_DebugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Cons.TAG, "personal/debug/Personal_DebugApplication");
    }
}
