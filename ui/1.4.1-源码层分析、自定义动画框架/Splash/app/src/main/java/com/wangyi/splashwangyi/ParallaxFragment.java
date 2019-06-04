package com.wangyi.splashwangyi;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ParallaxFragment  extends Fragment {
    //此Fragment上所有的需要实现视差动画的视图
    private List<View> parallaxViews = new ArrayList<View>();

    @Override
    public View onCreateView(LayoutInflater original, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        int layoutId = args.getInt("layoutId");
        ParallaxLayoutInflater inflater = new ParallaxLayoutInflater(original, getActivity(),this);
        return inflater.inflate(layoutId, null);
    }

    public List<View> getParallaxViews() {
        return parallaxViews;
    }

}
