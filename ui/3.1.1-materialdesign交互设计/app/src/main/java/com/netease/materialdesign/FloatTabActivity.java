package com.netease.materialdesign;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.netease.materialdesign.adapter.FragmentsAdapter;
import com.netease.materialdesign.fragment.MovieFragment;
import com.netease.materialdesign.fragment.MoviesFragment;
import com.netease.materialdesign.net.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;


public class FloatTabActivity extends AppCompatActivity {


    private LinearLayout            headLayout;
    private AppBarLayout            appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Window                  window;
    private ViewPager               viewPager;
    private ArrayList<Fragment>     fragments = new ArrayList<>();
    private TabLayout               tabLayout;
    private Banner                  banner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_float);
        window = getWindow();
        Toolbar toolbar = findViewById(R.id.tb_atf_toolbar);
        headLayout = findViewById(R.id.head_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        tabLayout = findViewById(R.id.toolbar_tab);
        viewPager = findViewById(R.id.main_vp_container);


        banner = findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        Integer[] images = {R.mipmap.v0, R.mipmap.v1, R.mipmap.v2, R.mipmap.v3};
        banner.setBannerAnimation(Transformer.ZoomIn);
        banner.setImages(Arrays.asList(images));
        //banner设置方法全部调用完毕时最后调用
        banner.start();


        setToolbar(toolbar);

        setTitleToCollapsingToolbarLayout();


        //相互绑定，这样可以防止图标被替换
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
                (tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener
                (viewPager));


        //tabItem图标显示不出来
        //tabLayout.setupWithViewPager(viewPager);

        initFragments();

        viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(5);
    }

    private void initFragments() {
        fragments.add(new MovieFragment());
        fragments.add(new MoviesFragment());
        fragments.add(new MoviesFragment());
        fragments.add(new MoviesFragment());
        fragments.add(new MoviesFragment());


    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * 使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，
     * 设置到Toolbar上则不会显示
     */
    private void setTitleToCollapsingToolbarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -headLayout.getHeight() / 2) {
                    collapsingToolbarLayout.setTitle("MaterialDesign");
                    //使用下面两个CollapsingToolbarLayout的方法设置展开透明->折叠时你想要的颜色
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

                    window.setStatusBarColor(getResources().getColor(R.color.fuck));
                } else {
                    collapsingToolbarLayout.setTitle("");
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }


}
