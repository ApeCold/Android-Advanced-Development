package com.netease.dagger2customproject.test;

import com.netease.custom_dagger2.ann.Inject;
import com.netease.dagger2customproject.DaggerWorkComponent;

public class ClassB {

    @Inject
    private ClassA classA;

    public void setClassA() {
        // DaggerWorkComponent.create().inject(this);
    }

}
