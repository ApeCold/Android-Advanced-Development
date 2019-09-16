package com.netease.pmsproject.pms;

public class MyContext {

    public MyPackageManager getPackageManager() {
        return new MyApplicationPackageManager();
    }

}
