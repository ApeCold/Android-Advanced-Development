package com.netease.skin.library.model;

import android.content.res.Resources;

public class SkinCache {

    private Resources skinResources; // 用于加载皮肤包资源
    private String skinPackageName; // 皮肤包资源所在包名（注：皮肤包不在app内，也不限包名）

    public SkinCache(Resources skinResources, String skinPackageName) {
        this.skinResources = skinResources;
        this.skinPackageName = skinPackageName;
    }

    public Resources getSkinResources() {
        return skinResources;
    }

    public String getSkinPackageName() {
        return skinPackageName;
    }
}
