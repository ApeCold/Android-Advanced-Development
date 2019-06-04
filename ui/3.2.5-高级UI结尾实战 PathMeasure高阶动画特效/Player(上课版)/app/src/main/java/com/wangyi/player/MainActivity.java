package com.wangyi.player;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.util.Util;
import com.wangyi.player.ui.UIUtils;
import com.wangyi.player.view.BackgourndAnimationRelativeLayout;
import com.wangyi.player.view.DiscView;
import com.wangyi.player.view.MusicLitener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends AppCompatActivity implements MusicLitener {

    private List<Integer> mMusicDatas = new ArrayList<>();
    private BackgourndAnimationRelativeLayout backgourndAnimationRelativeLayout;
    DiscView mDisc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.getInstance(this);
        setContentView(R.layout.activity_main);
        mDisc = (DiscView) findViewById(R.id.discview);
        mDisc.setMusicLitener(this);
        backgourndAnimationRelativeLayout = findViewById(R.id.rootLayout);
        mMusicDatas.add(R.drawable.ic_music1);
        mMusicDatas.add(R.drawable.ic_music2);
        mMusicDatas.add(R.drawable.ic_music3);
        mMusicDatas.add(R.drawable.ic_music1);
        mMusicDatas.add(R.drawable.ic_music2);
        mMusicDatas.add(R.drawable.ic_music3);
        mMusicDatas.add(R.drawable.ic_music1);
        mMusicDatas.add(R.drawable.ic_music2);
        mMusicDatas.add(R.drawable.ic_music3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDisc.setMusicDataList(mMusicDatas);
    }

    @Override
    public void onMusicPicChanged(int resID) {
        Glide.with(this)
                .load(resID)
                .crossFade(500)
                .bitmapTransform(new BlurTransformation(this, 200, 3))
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        backgourndAnimationRelativeLayout.setForeground(resource);
                    }
                });

    }
}
