package com.netease.customokhttp.chain2;

public class Test {

    public static void main(String[] args) {

        ChainManager chainManager = new ChainManager();

        chainManager.addTask(new Task1());
        chainManager.addTask(new Task2());
        chainManager.addTask(new Task3());

        chainManager.doRunAction("ok", chainManager);

    }

}
