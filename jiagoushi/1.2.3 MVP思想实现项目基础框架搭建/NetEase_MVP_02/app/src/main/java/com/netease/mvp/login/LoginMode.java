package com.netease.mvp.login;

import com.netease.mvp.base.BaseModel;
import com.netease.mvp.bean.UserInfo;

// 接收到P层交给它的需求
public class LoginMode extends BaseModel<LoginPresenter, LoginContract.Model> {

    public LoginMode(LoginPresenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    public LoginContract.Model getContract() {
        return new LoginContract.Model() {
            @Override
            public void executeLogin(String name, String pwd) throws Exception {
                if ("netease".equalsIgnoreCase(name) && "163".equals(pwd)) {
                    p.getContract().responseResult(new UserInfo("网易", "彭老师"));
                } else {
                    p.getContract().responseResult(null);
                }
            }
        };
    }
}
