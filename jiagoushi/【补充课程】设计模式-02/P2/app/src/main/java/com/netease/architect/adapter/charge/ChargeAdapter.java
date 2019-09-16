package com.netease.architect.adapter.charge;

public class ChargeAdapter extends ChinaCharge {

    private USACharge usaCharge;

    public ChargeAdapter(USACharge usaCharge) {
        this.usaCharge = usaCharge;
    }

    @Override
    public void chinaCharge() {
        // 如果想要在中国用电，必须是220V
        if (usaCharge.usaCharge() == 220) {
            System.out.println("符合中国220V用电标准");
        } else {
            System.out.println("用电异常，电器烧毁");
        }
    }
}
