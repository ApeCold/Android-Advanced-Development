package com.netease.arouter.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.netease.arouter.api.core.Call;

/**
 * Bundle拼接参数管理类
 */
public final class BundleManager {

    private Bundle bundle = new Bundle();
    // 底层业务接口
    private Call call;
    // 是否回调setResult()
    private boolean isResult;

    Bundle getBundle() {
        return bundle;
    }

    Call getCall() {
        return call;
    }

    void setCall(Call call) {
        this.call = call;
    }

    boolean isResult() {
        return isResult;
    }

    // @NonNull不允许传null，@Nullable可以传null
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    // 示例代码，需要拓展
    public BundleManager withResultString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(@NonNull Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public Object navigation(Context context) {
        return RouterManager.getInstance().navigation(context, this, -1);
    }

    // 这里的code，可能是requestCode，也可能是resultCode。取决于isResult
    public Object navigation(Context context, int code) {
        return RouterManager.getInstance().navigation(context, this, code);
    }
}
