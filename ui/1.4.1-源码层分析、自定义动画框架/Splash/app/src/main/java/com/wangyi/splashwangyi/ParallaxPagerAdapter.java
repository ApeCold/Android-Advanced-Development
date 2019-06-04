package com.wangyi.splashwangyi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ParallaxPagerAdapter extends FragmentPagerAdapter {

    private List<ParallaxFragment> fragments;

    public ParallaxPagerAdapter(FragmentManager fm,List<ParallaxFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
