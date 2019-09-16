package com.netease.architect.adapter;

import com.netease.architect.adapter.charge.USACharge;

public class USA extends USACharge {

    @Override
    public int usaCharge() {
        System.out.println("工程师改造了电压……");
        return 220;
    }
}
