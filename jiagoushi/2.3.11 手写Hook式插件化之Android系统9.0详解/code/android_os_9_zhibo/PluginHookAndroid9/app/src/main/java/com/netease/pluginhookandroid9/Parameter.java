package com.netease.pluginhookandroid9;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 所有的一些标记：例如：产量
 */
public interface Parameter {

    String TARGET_INTENT = "targetIntentMyTest"; // TODO 目标Activity

    int EXECUTE_TRANSACTION = 159; // TODO 在ActivityThread中即将还要去实例化Activity 会经过此Handler标记  适用于高版本

    int LAUNCH_ACTIVITY = 100; // TODO 在ActivityThread中即将还要去实例化Activity 会经过此Handler标记

    String PLUGIN_FILE_NAME = "plugin-debug.apk"; // TODO 插件名
}
