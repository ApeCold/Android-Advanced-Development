package com.netease.mvp.base;

import java.lang.ref.WeakReference;

// Presenter基类
public abstract class BasePresenter<V extends BaseView, M extends BaseModel, CONTRACT> {

    protected M m;
    // 绑定View层弱引用
    private WeakReference<V> vWeakReference;

    public BasePresenter() {
        m = getModel();
    }

    public void bindView(V v) {
        vWeakReference = new WeakReference<>(v);
    }

    public void unBindView() {
        if (vWeakReference != null) {
            vWeakReference.clear();
            vWeakReference = null;
            System.gc();
        }
    }

    // 获取View，P -- V
    public V getView() {
        if (vWeakReference != null) {
            return vWeakReference.get();
        }
        return null;
    }

    // 获取子类具体契约（Model层和View层协商的共同业务）
    public abstract CONTRACT getContract();

    public abstract M getModel();
}
