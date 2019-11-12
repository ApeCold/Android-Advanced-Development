package com.netease.customokhttp.build.com;

/**
 * 房子的图纸
 */
public class HouseParam {

    private double height;
    private double width;
    private String color = "白色";

    public HouseParam() {
    }

    public HouseParam(double height, double width, String color) {
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
        return "画出来的图纸：HouseParam{" +
                "height=" + height +
                ", width=" + width +
                ", color='" + color + '\'' +
                '}';
    }
}
