package com.netease.customokhttp.build.com;

/**
 * 300个员工
 */
public class Worker {

    // 拿到图纸
    private HouseParam houseParam;

    public void setHouseParam(HouseParam houseParam) {
        this.houseParam = houseParam;
    }

    // 工作 盖房子
    // 交付
    public House buildHouse() {
        // 盖房子 实例
        House house = new House();
        house.setColor(houseParam.getColor());
        house.setHeight(houseParam.getHeight());
        house.setWidth(houseParam.getWidth());
        return house;
    }

}
