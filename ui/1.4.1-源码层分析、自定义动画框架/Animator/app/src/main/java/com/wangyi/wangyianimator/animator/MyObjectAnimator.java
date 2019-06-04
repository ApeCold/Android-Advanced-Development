package com.wangyi.wangyianimator.animator;

import android.animation.ObjectAnimator;
import android.view.View;

import java.lang.ref.WeakReference;

public class MyObjectAnimator  implements VSYNCManager.AnimationFrameCallback {
    private static final String TAG = "tuch";
    long mStartTime = -1;
    private long mDuration = 0;
    private WeakReference<View> target;
    private float index = 0;
    private TimeInterpolator interpolator;
    MyFloatPropertyValuesHolder myFloatPropertyValuesHolder;
    public void setDuration(int duration) {
        this.mDuration = duration;
    }
    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    private MyObjectAnimator(View view, String propertyName, float... values) {
        target = new WeakReference<View>(view);
        myFloatPropertyValuesHolder = new MyFloatPropertyValuesHolder(propertyName, values);
    }

    public static MyObjectAnimator ofFloat(View view, String propertyName, float... values) {
        MyObjectAnimator anim = new MyObjectAnimator(view, propertyName, values);


        return anim;
    }
    public void start() {
        myFloatPropertyValuesHolder.setupSetter(target);
        mStartTime = System.currentTimeMillis();
        VSYNCManager.getInstance().add(this);
//初始化
    }
//回调  每隔16ms
    @Override
    public boolean doAnimationFrame(long currentTime) {
        float total= mDuration / 16;
//        执行百分比
        float fraction = (index++) / total;
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        if (index >=total) {
            index = 0;
        }
        myFloatPropertyValuesHolder.setAnimatedValue(target.get(),fraction);

        return false;
    }
}
