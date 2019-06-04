package com.netease.materialdesign.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    /**
     * 标记是否正在向上滑动
     */
    boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //当状态是不滑动的时候

            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();

            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                onLoadMoreData();
            }
        }
    }



    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0;
    }

    /**
     * 加载更多数据的方法
     */
    public abstract  void onLoadMoreData() ;

}
