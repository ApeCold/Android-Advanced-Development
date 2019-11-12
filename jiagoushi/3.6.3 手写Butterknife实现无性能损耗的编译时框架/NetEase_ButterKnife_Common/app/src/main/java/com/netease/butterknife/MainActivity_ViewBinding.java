//package com.netease.butterknife;
//
//import android.support.annotation.CallSuper;
//import android.support.annotation.UiThread;
//import android.view.View;
//import android.widget.TextView;
//
//import butterknife.internal.DebouncingOnClickListener;
//import butterknife.internal.Utils;
//
//public class MainActivity_ViewBinding {
//
//    private MainActivity target;
//
//    private View view7f070080;
//
//    private View view7f070081;
//
//    @UiThread
//    public MainActivity_ViewBinding(MainActivity target) {
//        this(target, target.getWindow().getDecorView());
//    }
//
//    @UiThread
//    public MainActivity_ViewBinding(final MainActivity target, View source) {
//        this.target = target;
//
//        View view;
//        view = Utils.findRequiredView(source, R.id.tv1, "field 'tv1' and method 'click'");
//        target.tv1 = Utils.castView(view, R.id.tv1, "field 'tv1'", TextView.class);
//        view7f070080 = view;
//        view.setOnClickListener(new DebouncingOnClickListener() {
//            @Override
//            public void doClick(View p0) {
//                target.click(p0);
//            }
//        });
//        view = Utils.findRequiredView(source, R.id.tv2, "field 'tv2' and method 'click2'");
//        target.tv2 = Utils.castView(view, R.id.tv2, "field 'tv2'", TextView.class);
//        view7f070081 = view;
//        view.setOnClickListener(new DebouncingOnClickListener() {
//            @Override
//            public void doClick(View p0) {
//                target.click2();
//            }
//        });
//        target.tv3 = Utils.findRequiredViewAsType(source, R.id.tv3, "field 'tv3'", TextView.class);
//        target.tv4 = Utils.findRequiredViewAsType(source, R.id.tv4, "field 'tv4'", TextView.class);
//    }
//
//    @Override
//    @CallSuper
//    public void unbind() {
//        MainActivity target = this.target;
//        if (target == null) throw new IllegalStateException("Bindings already cleared.");
//        this.target = null;
//
//        target.tv1 = null;
//        target.tv2 = null;
//        target.tv3 = null;
//        target.tv4 = null;
//
//        view7f070080.setOnClickListener(null);
//        view7f070080 = null;
//        view7f070081.setOnClickListener(null);
//        view7f070081 = null;
//    }
//}
