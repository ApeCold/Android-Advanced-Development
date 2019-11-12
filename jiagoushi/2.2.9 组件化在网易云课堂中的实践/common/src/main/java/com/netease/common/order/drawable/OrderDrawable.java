package com.netease.common.order.drawable;

import com.netease.arouter.api.core.Call;

/**
 * 订单模块对外暴露接口，其他模块可以获取返回res资源
 */
public interface OrderDrawable extends Call {

    int getDrawable();
}
