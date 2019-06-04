package com.netease.taobao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.bumptech.glide.Glide;
import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    //应用
    String[] ITEM_NAMES = {"天猫", "聚划算", "天猫国际", "外卖", "天猫超市", "充值中心", "飞猪旅行", "领金币", "拍卖", "分类"};
    int[] IMG_URLS = {R.mipmap.ic_tian_mao, R.mipmap.ic_ju_hua_suan, R.mipmap.ic_tian_mao_guoji, R.mipmap.ic_waimai, R.mipmap.ic_chaoshi, R.mipmap.ic_voucher_center, R.mipmap.ic_travel, R.mipmap.ic_tao_gold, R.mipmap.ic_auction, R.mipmap.ic_classify};

    //    高颜值商品位
    int[] ITEM_URL = {R.mipmap.item1, R.mipmap.item2, R.mipmap.item3, R.mipmap.item4, R.mipmap.item5};
    int[] GRID_URL = {R.mipmap.flashsale1, R.mipmap.flashsale2, R.mipmap.flashsale3, R.mipmap.flashsale4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        mRecyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mRecyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);
        BaseDelegeteAdapter bannerAdapter = new BaseDelegeteAdapter(this
                , new LinearLayoutHelper(), R.layout.vlayout_banner, 1){
            @Override
            public void onBindViewHolder(BaseViewHolder holder, int i) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("http://dn.dengpaoedu.com/examples/glide/1.jpg");
                arrayList.add("http://dn.dengpaoedu.com/examples/glide/2.jpg");
                arrayList.add("http://dn.dengpaoedu.com/examples/glide/3.jpg");
                arrayList.add("http://dn.dengpaoedu.com/examples/glide/4.jpg");
                arrayList.add("http://dn.dengpaoedu.com/examples/glide/5.jpg");
                arrayList.add("http://dn.dengpaoedu.com/examples/glide/6.jpg");
                // 绑定数据
                Banner mBanner = holder.getView(R.id.banner);
                //设置banner样式
                mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                //设置图片加载器
                mBanner.setImageLoader(new GlideImageLoader());
                //设置图片集合
                mBanner.setImages(arrayList);
                //设置banner动画效果
                mBanner.setBannerAnimation(Transformer.DepthPage);
                //设置标题集合（当banner样式有显示title时）
                //        mBanner.setBannerTitles(titles);
                //设置自动轮播，默认为true
                mBanner.isAutoPlay(true);
                //设置轮播时间
                mBanner.setDelayTime(3000);
                //设置指示器位置（当banner模式中有指示器时）
                mBanner.setIndicatorGravity(BannerConfig.CENTER);
                //banner设置方法全部调用完毕时最后调用
                mBanner.start();

                mBanner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Toast.makeText(getApplicationContext(), "banner点击了" + position, Toast.LENGTH_SHORT).show();
                    }
                });

                super.onBindViewHolder(holder, i);
            }
        };
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(5);
        gridLayoutHelper.setPadding(0, 16, 0, 0);
        gridLayoutHelper.setVGap(10);
        gridLayoutHelper.setHGap(0);//// 控制子元素之间的水平间距


        BaseDelegeteAdapter menuAdapter = new BaseDelegeteAdapter(this, gridLayoutHelper, R.layout.vlayout_menu, 10){
            @Override
            public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
                holder.setText(R.id.tv_menu_title_home, ITEM_NAMES[position] + "");
                holder.setImageResource(R.id.iv_menu_home, IMG_URLS[position]);
                holder.getView(R.id.ll_menu_home).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), ITEM_NAMES[position], Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        BaseDelegeteAdapter newsAdapter = new BaseDelegeteAdapter(this, new LinearLayoutHelper(),
                R.layout.vlayout_news, 1){
            @Override
            public void onBindViewHolder(@NonNull BaseViewHolder holder, int i) {
                MarqueeView marqueeView1 = holder.getView(R.id.marqueeView1);
                MarqueeView marqueeView2 = holder.getView(R.id.marqueeView2);

                List<String> info1 = new ArrayList<>();
                info1.add("天猫超市最近发大活动啦，快来抢");
                info1.add("没有最便宜，只有更便宜！");

                List<String> info2 = new ArrayList<>();
                info2.add("这个是用来搞笑的，不要在意这写小细节！");
                info2.add("啦啦啦啦，我就是来搞笑的！");

                marqueeView1.startWithList(info1);
                marqueeView2.startWithList(info2);
                // 在代码里设置自己的动画
                marqueeView1.startWithList(info1, R.anim.anim_bottom_in, R.anim.anim_top_out);
                marqueeView2.startWithList(info2, R.anim.anim_bottom_in, R.anim.anim_top_out);

                marqueeView1.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, TextView textView) {
                        Toast.makeText(getApplicationContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                marqueeView2.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, TextView textView) {
                        Toast.makeText(getApplicationContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager, true);
        delegateAdapter.addAdapter(bannerAdapter);
        delegateAdapter.addAdapter(menuAdapter);
        delegateAdapter.addAdapter(newsAdapter);
        for (int i = 0; i < ITEM_URL.length; i++) {
            final int finalI = i;
            BaseDelegeteAdapter titleAdapter = new BaseDelegeteAdapter(this,
                    new LinearLayoutHelper(), R.layout.vlayout_title, 1) {
                @Override
                public void onBindViewHolder(BaseViewHolder holder, int position) {
                    super.onBindViewHolder(holder, position);
                    holder.setImageResource(R.id.iv, ITEM_URL[finalI]);
                }
            };
            GridLayoutHelper gridHelper = new GridLayoutHelper(2);
            BaseDelegeteAdapter gridAdapter = new BaseDelegeteAdapter(this, gridHelper,
                    R.layout.vlayout_grid, 4){

                @Override
                public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
                    int item = GRID_URL[position];
                    ImageView iv = holder.getView(R.id.iv);
                    Glide.with(getApplicationContext()).load(item).into(iv);

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "item" + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
            delegateAdapter.addAdapter(titleAdapter);
            delegateAdapter.addAdapter(gridAdapter);
        }

        mRecyclerView.setAdapter(delegateAdapter);
    }

//    class BannerAdapter   extends DelegateAdapter.Adapter<BaseViewHolder>{
//
//
//        private Context mContext;
//
//        public BannerAdapter(Context context) {
//            mContext = context;
//        }
//        @Override
//        public LayoutHelper onCreateLayoutHelper() {
//            return new LinearLayoutHelper();
//        }
//
//        @NonNull
//        @Override
//        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//            return  new BaseViewHolder(LayoutInflater.from(mContext).inflate( R.layout.vlayout_banner, viewGroup, false));
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
//            ArrayList<String> arrayList = new ArrayList<>();
//            arrayList.add("http://dn.dengpaoedu.com/examples/glide/1.jpg");
//            arrayList.add("http://dn.dengpaoedu.com/examples/glide/2.jpg");
//            arrayList.add("http://dn.dengpaoedu.com/examples/glide/3.jpg");
//            arrayList.add("http://dn.dengpaoedu.com/examples/glide/4.jpg");
//            arrayList.add("http://dn.dengpaoedu.com/examples/glide/5.jpg");
//            arrayList.add("http://dn.dengpaoedu.com/examples/glide/6.jpg");
//            // 绑定数据
//            Banner mBanner = baseViewHolder.getView(R.id.banner);
//            //设置banner样式
//            mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
//            //设置图片集合
//            mBanner.setImages(arrayList);
//            //设置banner动画效果
//            mBanner.setBannerAnimation(Transformer.DepthPage);
//            //设置标题集合（当banner样式有显示title时）
//            //        mBanner.setBannerTitles(titles);
//            //设置自动轮播，默认为true
//            mBanner.isAutoPlay(true);
//            mBanner.setImageLoader(new GlideImageLoader());
//            //设置轮播时间
//            mBanner.setDelayTime(3000);
//            //设置指示器位置（当banner模式中有指示器时）
//            mBanner.setIndicatorGravity(BannerConfig.CENTER);
//            //banner设置方法全部调用完毕时最后调用
//            mBanner.start();
//
//            mBanner.setOnBannerListener(new OnBannerListener() {
//                @Override
//                public void OnBannerClick(int position) {
//                    Toast.makeText(getApplicationContext(), "banner点击了" + position, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        @Override
//        public int getItemCount() {
//            return 1;
//        }
//    }
}
