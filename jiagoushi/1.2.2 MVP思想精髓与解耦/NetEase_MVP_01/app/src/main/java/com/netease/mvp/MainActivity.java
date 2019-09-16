package com.netease.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.mvp.model.ImageBean;
import com.netease.mvp.presenter.DownLoaderPresenter;
import com.netease.mvp.utils.Constant;

// MVC中Activity是C层，MVP中Activity是V层
public class MainActivity extends AppCompatActivity implements DownloaderContract.PV {

    private ImageView imageView;
    private DownLoaderPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.iv);
        presenter = new DownLoaderPresenter(this);
    }

    // 点击事件
    public void down(View view) {
        ImageBean imageBean = new ImageBean();
        imageBean.setRequestPath(Constant.IMAGE_PATH);
        requestDownloader(imageBean);
    }

    @Override
    public void requestDownloader(ImageBean imageBean) {
        if (presenter != null) presenter.requestDownloader(imageBean);
    }

    @Override
    public void responseDownloaderResult(boolean isSuccess, ImageBean imageBean) {
        Toast.makeText(this, isSuccess ? "下载成功" : "下载失败", Toast.LENGTH_SHORT).show();
        if (isSuccess && imageBean.getBitmap() != null) {
            imageView.setImageBitmap(imageBean.getBitmap());
        }
    }
}
