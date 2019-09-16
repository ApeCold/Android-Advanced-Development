package com.netease.archtect.factory.Impl;

import com.netease.archtect.factory.Api;

public class ParameterFactory {

    public  static Api createApi(int parameter) {
        switch (parameter) {
            case 1:
                return new ApiImpl_A();

            case 2:
                return new ApiImpl_B();
        }
        return null;
    }
}
