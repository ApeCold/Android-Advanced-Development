//package com.netease.butterknife;
//
//import android.view.View;
//
//public class DebouncingOnClickListener {
//
//    static boolean enabled = true;
//
//    private static final Runnable ENABLE_AGAIN = new Runnable() {
//        @Override public void run() {
//            enabled = true;
//        }
//    };
//
//    @Override public final void onClick(View v) {
//        if (enabled) {
//            enabled = false;
//            v.post(ENABLE_AGAIN);
//            doClick(v);
//        }
//    }
//
//    public abstract void doClick(View v);
//}
