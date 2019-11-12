package com.netease.customokhttp.build;

import com.netease.customokhttp.build.com.DisignPerson;
import com.netease.customokhttp.build.com.House;

// 用户
public class UserClient {

    // 第一版
//    public static void main(String[] args) {
//        // 找到 -->建筑公司 -- DisignPerson
//
//        DisignPerson disignPerson = new DisignPerson();
//        //  -- 画图纸的过程
//        disignPerson.addColor("白色");
//        disignPerson.addWidth(120.00);
//        disignPerson.addHeight(4);
//
//        disignPerson.addColor("绿色");
//        disignPerson.addWidth(100.00);
//        disignPerson.addHeight(2);
//
//        disignPerson.addColor("蓝色");
//        disignPerson.addWidth(80.00);
//        disignPerson.addHeight(2);
//
//        disignPerson.addColor("红色");
//        disignPerson.addWidth(90.00);
//        disignPerson.addHeight(3);
//
//        // 复制的过程
//
//        //  -- 盖房子的过程
//        House house = disignPerson.build();
//        System.out.println(house);
//    }

    // 第二版，链式调用
    public static void main(String[] args) {
        House house = new DisignPerson().addColor("白色").addWidth(100).addHeight(6).addHeight(9).addHeight(8).build();
        System.out.println(house);
    }
}
