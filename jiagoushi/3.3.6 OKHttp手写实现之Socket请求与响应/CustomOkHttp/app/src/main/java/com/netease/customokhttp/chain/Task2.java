package com.netease.customokhttp.chain;

public class Task2 extends BaseTask {

    public Task2(boolean isTask) {
        super(isTask);
    }

    @Override
    public void doAction() {
        // // 执行 子节点 链条断
        System.out.println("Task1 任务节点二 执行了");
    }
}
