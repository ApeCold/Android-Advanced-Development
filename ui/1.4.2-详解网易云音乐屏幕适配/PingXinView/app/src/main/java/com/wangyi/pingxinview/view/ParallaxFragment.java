package com.wangyi.pingxinview.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ParallaxFragment  extends Fragment {


    //    加载布局  了解有哪些需要差异化划定
//此Fragment上所有的需要实现视差动画的视图
private List<View> parallaxViews = new ArrayList<View>();
    @Override
    public View onCreateView(LayoutInflater original, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        int layoutId = args.getInt("layoutId");
        ParallaxLayoutInflater parallaxLayoutInflater = new ParallaxLayoutInflater(original, getActivity(), this);
        return parallaxLayoutInflater.inflate(layoutId, null);
    }

    public List<View> getParallaxViews() {
        return parallaxViews;
    }
}
