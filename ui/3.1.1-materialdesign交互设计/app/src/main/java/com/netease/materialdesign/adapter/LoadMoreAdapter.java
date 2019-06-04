package com.netease.materialdesign.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.materialdesign.R;
import com.netease.materialdesign.bean.Movie;
import com.netease.materialdesign.view.LoadingView;

import java.util.ArrayList;

/**
 * 上拉加载更多的adapter
 */
public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {



    // 普通布局
    public final int TYPE_ITEM = 1;
    // 脚布局
    public final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;


    private ArrayList<Movie.SubjectsBean> mData;
    private Context                       mContext;

    public LoadMoreAdapter(ArrayList<Movie.SubjectsBean> SubjectsBean, Context context) {
        this.mData = SubjectsBean;
        this.mContext = context;
    }


    @Override
    public int getItemCount() {
        //数据加1
        return mData.size() + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            //底部的布局
            return TYPE_FOOTER;
        } else {
            //正常的布局
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            view.setOnClickListener(this);
            return new RecyclerViewItemHolder(view);
        } else if (viewType == TYPE_FOOTER) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_refresh_footer, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //正常布局的holder
        if (holder instanceof RecyclerViewItemHolder) {
            RecyclerViewItemHolder recyclerViewHolder = (RecyclerViewItemHolder) holder;

            recyclerViewHolder.tv_name.setText(mData.get(position).getTitle());
            recyclerViewHolder.tv_star.setText(mData.get(position).getYear());
            recyclerViewHolder.tv_year.setText(mData.get(position).getRating().getAverage() + "");
            recyclerViewHolder.tv_avatars.setText(mData.get(position).getCasts().get(0).getName());
            Glide.with(mContext).load(mData.get(position).getImages().getMedium())
                    .centerCrop()
                    .crossFade(2000)
                    .into(recyclerViewHolder.iv_icon);
            holder.itemView.setTag(position);

        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING:
                    //正在加载中
                    footViewHolder.lvAtlvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.lvAtlvLoading.addBitmap(R.mipmap.v4);
                    footViewHolder.lvAtlvLoading.addBitmap(R.mipmap.v5);
                    footViewHolder.lvAtlvLoading.addBitmap(R.mipmap.v6);
                    footViewHolder.lvAtlvLoading.addBitmap(R.mipmap.v7);
                    footViewHolder.lvAtlvLoading.addBitmap(R.mipmap.v8);
                    footViewHolder.lvAtlvLoading.addBitmap(R.mipmap.v9);
                    footViewHolder.lvAtlvLoading.setShadowColor(Color.LTGRAY);
                    footViewHolder.lvAtlvLoading.setDuration(300);
                    footViewHolder.lvAtlvLoading.start();
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    footViewHolder.llWarn.setVisibility(View.GONE);

                    break;
                case LOADING_COMPLETE:
                    //加载完成，还有数据
                    footViewHolder.lvAtlvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    footViewHolder.llWarn.setVisibility(View.VISIBLE);

                    break;
                case LOADING_END:
                    //没有数据
                    footViewHolder.lvAtlvLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    footViewHolder.llWarn.setVisibility(View.GONE);
                    break;
            }
        }
    }


//    @Override
//    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if (manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
//                    Log.w("Tag",gridManager.getSpanCount()+"--------");
//                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
//                }
//            });
//        }
//    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    Log.w("Tag", gridManager.getSpanCount() + "--------");
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }


    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        mOnItemClickListener.onItemClick(v, (int) v.getTag());
    }


    /**
     * 正常条目的item的ViewHolder
     */
    private class RecyclerViewItemHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public ImageView iv_icon;
        public TextView tv_year;
        public TextView tv_avatars;
        public TextView tv_star;


        public RecyclerViewItemHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_avatars = itemView.findViewById(R.id.tv_avatars);
            tv_year = itemView.findViewById(R.id.tv_year);
            tv_star = itemView.findViewById(R.id.tv_star);
            iv_icon = itemView.findViewById(R.id.iv_icon);

        }
    }

    /**
     * FootView的Holder
     */
    private class FootViewHolder extends RecyclerView.ViewHolder {

        /**
         * 进度条展示
         */
        LoadingView  lvAtlvLoading;
        /**
         * 正在加载的TextView
         */
        TextView     tvLoading;
        /**
         * 服务器没有数据信息展示
         */
        LinearLayout llEnd;
        /**
         * 进行提示的布局信息
         */
        LinearLayout llWarn;

        FootViewHolder(View itemView) {
            super(itemView);
            lvAtlvLoading = itemView.findViewById(R.id.pb_loading);
            tvLoading = itemView.findViewById(R.id.tv_loading);
            llEnd = itemView.findViewById(R.id.ll_end);
            llWarn = itemView.findViewById(R.id.ll_warn);
        }
    }


    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener = null;


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
