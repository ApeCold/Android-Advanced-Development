package com.netease.customokhttp.build.com;

/**
 * 真实存在的房子
 */
public class House {

    private double height;
    private double width;
    private String color;

    public House() {
    }

    public House(double height, double width, String color) {
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "具体建造出来的房子>>>>：HouseParam{" +
                "height=" + height +
                ", width=" + width +
                ", color='" + color + '\'' +
                '}';
    }

}
