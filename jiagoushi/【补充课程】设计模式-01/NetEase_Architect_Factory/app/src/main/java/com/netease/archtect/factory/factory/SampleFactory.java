package com.netease.archtect.factory.factory;

import com.netease.archtect.factory.Api;
import com.netease.archtect.factory.ApiImpl;

public class SampleFactory {

    public static Api createApi() {
        return new ApiImpl();
    }
}
