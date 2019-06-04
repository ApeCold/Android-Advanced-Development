package com.netease.materialdesign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.netease.materialdesign.adapter.EndlessRecyclerOnScrollListener;
import com.netease.materialdesign.adapter.LoadMoreAdapter;
import com.netease.materialdesign.bean.Movie;

import java.util.ArrayList;


/**
 * 带有ToolbarActivity的使用
 */
public class ToolbarActivity extends AppCompatActivity {

    private DrawerLayout       dlAtDrawLayout;
    private ArrayList<String>  mData = new ArrayList<>();
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView       mRvList;
    private Toolbar            tbAtToolbar;
    private LoadMoreAdapter    loadMoreAdapter;
    private Intent             intent;

    private int start = 0;
    private int end   = 20;

    private ArrayList<Movie.SubjectsBean> mMovieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_toolbar);

        //如果不设置，则不会出现标题
        Toolbar tbAtToolbar = findViewById(R.id.tb_at_toolbar);
        //不设置会显示label的属性,也可以在清单文件中进行配置
//      tbAtToolbar.setTitle(" I am toolbar ");
        setSupportActionBar(tbAtToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //设置Toolbar home键可点击
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置Toolbar home键图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_am);
        }

        //Toolbar关联侧滑菜单
        dlAtDrawLayout = findViewById(R.id.dl_at_draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, dlAtDrawLayout, tbAtToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlAtDrawLayout.addDrawerListener(toggle);
        toggle.syncState();

        //浮动按钮
        FloatingActionButton fabButton = findViewById(R.id.fab_at_action);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出提示
                Snackbar.make(v, "snack action ", 1000)
                        //Snackbar点击响应
                        .setAction("Toast", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ToolbarActivity.this, " to do ", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        mRvList = findViewById(R.id.rv_at_list);
        srlRefresh = findViewById(R.id.srl_refresh);
        srlRefresh.setRefreshing(true);
        mRvList.setLayoutManager(new LinearLayoutManager(this));

        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMovieList.clear();
                end = 20;
                getNetData(start, end, true);
            }
        });

        getNetData(start, end, false);


        mRvList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMoreData() {
                loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mMovieList.size() < 250) {
                            getNetData(end, 20, false);
                            end += 20;
                        } else {
                            loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING_END);
                        }
                    }
                }, 2000);


            }
        });
    }

    private void getNetData(int start, int end, final boolean isRefresh) {
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());
        mMovieList.add(new Movie.SubjectsBean());

        if (loadMoreAdapter == null) {
            loadMoreAdapter = new LoadMoreAdapter(mMovieList, ToolbarActivity.this);
            mRvList.setAdapter(loadMoreAdapter);
        } else {
            loadMoreAdapter.notifyDataSetChanged();
        }

        srlRefresh.setRefreshing(false);
        initItemListener(mMovieList);

        loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING_COMPLETE);
    }


    private void initItemListener(final ArrayList<Movie.SubjectsBean> movie) {
        loadMoreAdapter.setOnItemClickListener(new LoadMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Activity共享元素转场动画
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ToolbarActivity.this,
                        view.findViewById(R.id.iv_icon),
                        "basic"
                );

                intent = new Intent(ToolbarActivity.this, MovieDetailActivity.class);
                intent.putExtra("URL", movie.get(position).getImages().getMedium());
                intent.putExtra("NAME", movie.get(position).getTitle());
                startActivity(intent, optionsCompat.toBundle());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobalr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                dlAtDrawLayout.openDrawer(Gravity.START);
                break;
            case R.id.add:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tb_setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


}
