package com.netease.architect.adapter;

import com.netease.architect.adapter.charge.ChargeAdapter;
import com.netease.architect.adapter.charge.ChinaCharge;

import org.junit.Test;

public class ChargeUnitTest {

    @Test
    public void charge() {

        // 用到了适配器
        ChinaCharge chinaCharge = new ChargeAdapter(new USA());
        chinaCharge.chinaCharge();
    }
}
