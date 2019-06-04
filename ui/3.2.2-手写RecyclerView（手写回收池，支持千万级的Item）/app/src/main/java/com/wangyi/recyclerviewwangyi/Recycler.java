package com.wangyi.recyclerviewwangyi;

import android.view.View;

import java.util.Stack;

public class Recycler {
    private Stack<View>[] views ;
    public Recycler(int typeNumber) {
        views = new Stack[typeNumber];
        for (int i=0;i<typeNumber;i++) {

            views[i] = new Stack<View>();
        }
    }
    public void put(View view, int type){
        views[type].push(view);
    }
    public View get(int type) {
        try {
            return views[type].pop();
        } catch (Exception e) {
            return null;
        }


    }


}
