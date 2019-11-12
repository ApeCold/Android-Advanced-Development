package com.netease.customglide;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

/**
 * 管理RequestManager
 */
public class RequestManagerRetriver {

    public RequestManager get(FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity) {
        return new RequestManager(activity);
    }

    public RequestManager get(Context context) {
        return new RequestManager(context);
    }
}
