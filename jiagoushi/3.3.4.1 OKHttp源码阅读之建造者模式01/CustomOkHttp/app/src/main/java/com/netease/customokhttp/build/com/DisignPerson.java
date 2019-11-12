package com.netease.customokhttp.build.com;

/**
 * 设计师
 */
public class DisignPerson {

    // 画图纸
    private HouseParam houseParam;

    // 员工
    private Worker worker;

    public DisignPerson() {
        houseParam = new HouseParam();
        worker = new Worker();
    }

    /**
     * 增加楼层 -- 画图纸的过程
     */
    public DisignPerson addHeight(double height) {
        houseParam.setHeight(height);
        return this;
    }

    /**
     * 增加面积 -- 画图纸的过程
     */
    public DisignPerson addWidth(double width) {
        houseParam.setWidth(width);
        return this;
    }

    /**
     * 增加颜色 -- 画图纸的过程
     */
    public DisignPerson addColor(String color) {
        houseParam.setColor(color);
        return this;
    }

    /**
     * 把图纸给工人
     * 员工说房子盖好了
     * @return
     */
    public House build() {
        worker.setHouseParam(houseParam);
        return worker.buildHouse();
    }
}
