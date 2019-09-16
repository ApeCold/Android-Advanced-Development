package com.netease.mvp.http_lib;

import com.netease.mvp.bean.UserInfo;
import com.netease.mvp.login.LoginPresenter;

// 有可能是共有的Model
public class HttpEngine<P extends LoginPresenter> {

    private P p;

    public HttpEngine(P p) {
        this.p = p;
    }

    public void post(String name, String pwd) {
        if ("netease".equalsIgnoreCase(name) && "163".equals(pwd)) {
            p.getContract().responseResult(new UserInfo("网易", "彭老师"));
        } else {
            p.getContract().responseResult(null);
        }
    }
}
