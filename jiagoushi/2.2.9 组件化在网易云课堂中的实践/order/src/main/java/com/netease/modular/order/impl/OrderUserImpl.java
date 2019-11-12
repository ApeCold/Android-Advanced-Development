package com.netease.modular.order.impl;

import com.netease.arouter.annotation.ARouter;
import com.netease.common.user.BaseUser;
import com.netease.common.user.IUser;
import com.netease.modular.order.model.UserInfo;

/**
 * personal模块实现的内容
 */
@ARouter(path = "/order/getUserInfo")
public class OrderUserImpl implements IUser {

    @Override
    public BaseUser getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("冯老师");
        userInfo.setAccount("netease_river");
        userInfo.setPassword("666666");
        userInfo.setVipLevel(9);
        return userInfo;
    }
}
