package com.netease.customglide.load_data;

import com.netease.customglide.resource.Value;

/**
 * 加载外部资源 成功与失败的 回调
 */
public interface ResponseListener {

    public void responseSuccess(Value value);

    public void responseException(Exception e);

}
