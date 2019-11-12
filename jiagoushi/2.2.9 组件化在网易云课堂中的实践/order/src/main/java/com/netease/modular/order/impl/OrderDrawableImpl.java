package com.netease.modular.order.impl;

import com.netease.arouter.annotation.ARouter;
import com.netease.common.order.drawable.OrderDrawable;
import com.netease.modular.order.R;

/**
 * 订单模块对外暴露接口实现类，其他模块可以获取返回res资源
 */
@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {

    @Override
    public int getDrawable() {
        return R.drawable.ic_ac_unit_black_24dp;
    }
}
