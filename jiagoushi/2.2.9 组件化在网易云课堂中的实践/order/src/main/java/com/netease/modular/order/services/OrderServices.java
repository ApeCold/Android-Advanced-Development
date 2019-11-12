package com.netease.modular.order.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 订单模块特有业务，其他模块最好不要干涉
 */
public interface OrderServices {

    @POST("/ip/ipNew")
    @FormUrlEncoded
    Call<ResponseBody> get(@Field("ip") String ip, @Field("key") String key);
}
