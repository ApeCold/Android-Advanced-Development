package com.netease.archtect.factory.Impl;

import android.util.Log;

import com.netease.archtect.factory.Api;
import com.netease.archtect.factory.bean.UserInfo;

public class ApiImpl_A implements Api {

    @Override
    public UserInfo create() {
        UserInfo info = new UserInfo("彭老师");
        Log.e("netease >>> ", info.toString());
        return info;
    }
}
