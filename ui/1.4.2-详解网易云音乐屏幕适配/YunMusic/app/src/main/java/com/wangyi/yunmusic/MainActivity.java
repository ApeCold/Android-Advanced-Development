package com.wangyi.yunmusic;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyi.yunmusic.ui.UIUtils;
import com.wangyi.yunmusic.ui.ViewCalculateUtil;
//UI一致性   首位
public class MainActivity extends AppCompatActivity {
    private TextView tvText3;
    private TextView tvText4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.getInstance(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        tvText3 = findViewById(R.id.tvText3);
        tvText4 = findViewById(R.id.tvText4);
        ViewCalculateUtil.setViewLinearLayoutParam(tvText3, 540, 100, 0, 0, 0, 0);
        ViewCalculateUtil.setViewLinearLayoutParam(tvText4, 1080, 100, 0, 0, 0, 0);
        ViewCalculateUtil.setTextSize(tvText3,30);
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UIUtils.notityInstance(this);
    }
}
