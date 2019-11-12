package com.netease.fastjson;

import com.netease.fastjson.library.JSON;
import com.netease.fastjson.model.OrderInfo;
import com.netease.fastjson.model.UserInfo;

import org.junit.Test;

import java.math.BigDecimal;

public class FastJsonTest {

    private String json;

    @Test
    public void serializer() {
        UserInfo user = new UserInfo();
        user.setuId("0");
        user.setNickName("simon");
        user.setRealName("彭锡");
        user.setEmail("simon@cmonbaby.com");
        user.setAddress("网易杭州研究院");
        user.setPhoneNumber("187*****257");
        user.setUserType(3);
        user.setHeaderImage("https://www.cmonbaby.com/images/avatar.jpg");

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setoId("S16320190729010001");
        orderInfo.setCategoryId("C163060006");
        orderInfo.setGoodsName("网易严选-网易自家的味央黑猪肉");
        orderInfo.setPrice(new BigDecimal("52.163"));
        orderInfo.setCreateDate(System.currentTimeMillis());
        orderInfo.setUserInfo(user);

        System.out.println("彭老师序列化----------------------------------");
        long t1 = System.currentTimeMillis();
        json = JSON.toJSONString(orderInfo);
        System.out.println(json);
        System.out.println("耗时：" + (System.currentTimeMillis() - t1) + " ms");

        System.out.println("FastJson序列化----------------------------------");
        long t2 = System.currentTimeMillis();
        json = com.alibaba.fastjson.JSON.toJSONString(orderInfo);
        System.out.println(json);
        System.out.println("耗时：" + (System.currentTimeMillis() - t2) + " ms");
        deserializer();
    }

    private void deserializer() {
        System.out.println("彭老师反序列化----------------------------------");
        long t1 = System.currentTimeMillis();
        OrderInfo orderInfo1 = JSON.parseObject(json, OrderInfo.class);
        if (orderInfo1 == null) return;
        System.out.println(orderInfo1.toString());
        System.out.println("耗时：" + (System.currentTimeMillis() - t1) + " ms");


        System.out.println("FastJson反序列化----------------------------------");
        long t2 = System.currentTimeMillis();
        OrderInfo orderInfo2 = com.alibaba.fastjson.JSON.parseObject(json, OrderInfo.class);
        if (orderInfo2 == null) return;
        System.out.println(orderInfo2.toString());
        System.out.println("耗时：" + (System.currentTimeMillis() - t2) + " ms");
    }
}
