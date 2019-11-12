package com.netease.skin.local;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.netease.skin.library.SkinActivity;
import com.netease.skin.library.utils.PreferencesUtils;

public class MainActivity extends SkinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        boolean isNight = PreferencesUtils.getBoolean(this, "isNight");
        if (isNight) {
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // 点击事件
    public void dayOrNight(View view) {
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (uiMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                PreferencesUtils.putBoolean(this, "isNight", true);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                PreferencesUtils.putBoolean(this, "isNight", false);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean openChangeSkin() {
        return true;
    }
}
