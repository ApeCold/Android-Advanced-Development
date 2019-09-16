package com.netease.mvp.presenter;

import com.netease.mvp.DownloaderContract;
import com.netease.mvp.MainActivity;
import com.netease.mvp.engine.DownLoaderEngine;
import com.netease.mvp.model.ImageBean;

// P层几乎不做事情？谷歌的sample中，P层是包揽了所有的活
public class DownLoaderPresenter implements DownloaderContract.PV {

    private MainActivity view;
    private DownLoaderEngine model; // 下载的模型

    public DownLoaderPresenter(MainActivity view) {
        this.view = view;
        model = new DownLoaderEngine(this);
    }

    @Override
    public void requestDownloader(ImageBean imageBean) {
        // 接收到View层的指令，去完成某个需求（可以自己完成，也可以让别人去完成）
        try {
            model.requestDownloader(imageBean);
        } catch (Exception e) {
            e.printStackTrace();
            // 省略了异常的处理
        }
    }

    @Override
    public void responseDownloaderResult(final boolean isSuccess, final ImageBean imageBean) {
        // 将完成的结果告知View层（刷新UI）
        view.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.responseDownloaderResult(isSuccess, imageBean);
            }
        });
    }
}
