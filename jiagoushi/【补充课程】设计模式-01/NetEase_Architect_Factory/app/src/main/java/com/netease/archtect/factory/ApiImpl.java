package com.netease.archtect.factory;

import android.util.Log;

import com.netease.archtect.factory.bean.UserInfo;

public class ApiImpl implements Api {

    @Override
    public UserInfo create() {
        UserInfo info = new UserInfo();
        Log.e("netease >>> ", info.toString());
        return null;
    }
}
