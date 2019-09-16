package com.netease.mvvm.model;

import android.databinding.ObservableField;

public class UserInfo {

    // 被观察的属性（切记：必须是public修饰符，因为是DataBinding的规范）
    public ObservableField<String> name = new ObservableField<>();

    public ObservableField<String> pwd = new ObservableField<>();
}
