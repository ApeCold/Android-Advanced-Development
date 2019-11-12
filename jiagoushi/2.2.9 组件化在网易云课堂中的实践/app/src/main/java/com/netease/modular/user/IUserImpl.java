package com.netease.modular.user;

import com.netease.arouter.annotation.ARouter;
import com.netease.common.user.BaseUser;
import com.netease.common.user.IUser;
import com.netease.modular.model.UserInfo;

/**
 * personal模块实现的内容
 */
@ARouter(path = "/app/getUserInfo")
public class IUserImpl implements IUser {

    @Override
    public BaseUser getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("彭老师");
        userInfo.setAccount("netease_simon");
        userInfo.setPassword("666666");
        userInfo.setVipLevel(9);
        return userInfo;
    }
}
