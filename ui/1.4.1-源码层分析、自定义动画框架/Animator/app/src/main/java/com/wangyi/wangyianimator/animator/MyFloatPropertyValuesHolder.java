package com.wangyi.wangyianimator.animator;

import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyFloatPropertyValuesHolder {
//    属性名
    String mPropertyName;
//float
    Class mValueType;
    MyKeyframeSet mKeyframes;
    Method mSetter = null;
    public MyFloatPropertyValuesHolder(String propertyName, float... values) {
        this.mPropertyName = propertyName;
        mValueType = float.class;
//        sleep (20ms)   invidate()  信号
        mKeyframes = MyKeyframeSet.ofFloat(values);
    }
    public void setupSetter(WeakReference<View> target) {
        char firstLetter = Character.toUpperCase(mPropertyName.charAt(0));
        String theRest = mPropertyName.substring(1);
        String methodName="set"+ firstLetter + theRest;
        try {
            mSetter = View.class.getMethod(methodName, float.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public void setAnimatedValue(View target, float fraction) {
        Object value = mKeyframes.getValue(fraction);
        try {
//            View.setScaleX(101px)
            mSetter.invoke(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
