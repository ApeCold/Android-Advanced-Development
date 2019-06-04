package com.wangyi.wangyianimator.animator;

import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;

import java.util.Arrays;
import java.util.List;

public class MyKeyframeSet {
//    类型估值器
    TypeEvaluator mEvaluator;
    MyFloatKeyframe mFirstKeyframe;
    List<MyFloatKeyframe> mKeyframes;
    public MyKeyframeSet(MyFloatKeyframe... keyframes) {
        mKeyframes = Arrays.asList(keyframes);
        mFirstKeyframe = keyframes[0];
        mEvaluator = new FloatEvaluator();
    }

    public static MyKeyframeSet ofFloat(float[] values) {
        int numKeyframes = values.length;

        MyFloatKeyframe keyframes[] = new MyFloatKeyframe[numKeyframes];
        keyframes[0] = new MyFloatKeyframe(0, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = new MyFloatKeyframe((float) i / (numKeyframes - 1), values[i]);
        }
        return new MyKeyframeSet(keyframes);
    }
    public Object getValue(float fraction) {

        MyFloatKeyframe prevKeyframe = mFirstKeyframe;
        for (int i = 1; i < mKeyframes.size(); ++i) {
            MyFloatKeyframe nextKeyframe = mKeyframes.get(i);
            nextKeyframe = mKeyframes.get(i);
            if (fraction < nextKeyframe.getFraction()) {
                return mEvaluator.evaluate(fraction, prevKeyframe.getValue(),
                        nextKeyframe.getValue());
            }
            prevKeyframe = nextKeyframe;
        }

        return null;
    }
}
