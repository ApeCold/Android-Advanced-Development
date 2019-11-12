package com.netease.fastjson.model;

import java.math.BigDecimal;

public class OrderInfo {

    private String oId; // 商品id
    private String categoryId; // 商品类型id
    private String goodsName; // 商品名称
    private BigDecimal price; // 商品价格
    private long createDate; // 订单创建时间
    private UserInfo userInfo; // 购买用户

    public OrderInfo() {
    }

    public String getoId() {
        return oId;
    }

    public void setoId(String oId) {
        this.oId = oId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "oId='" + oId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", createDate=" + createDate +
                ", userInfo=" + userInfo +
                '}';
    }
}
