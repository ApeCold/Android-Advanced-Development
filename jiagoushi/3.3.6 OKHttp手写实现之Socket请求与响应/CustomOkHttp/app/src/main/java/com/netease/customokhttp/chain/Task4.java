package com.netease.customokhttp.chain;

public class Task4 extends BaseTask {

    public Task4(boolean isTask) {
        super(isTask);
    }

    @Override
    public void doAction() {
        // // 执行 子节点 链条断
        System.out.println("Task1 任务节点四 执行了");
    }
}
