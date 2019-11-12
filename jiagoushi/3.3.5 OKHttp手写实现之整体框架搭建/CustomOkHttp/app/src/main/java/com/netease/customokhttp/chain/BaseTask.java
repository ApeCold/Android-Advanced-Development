package com.netease.customokhttp.chain;

public abstract class BaseTask {

    // 判断当前任务节点 有没有能力执行  有
    private boolean isTask;

    public BaseTask(boolean isTask) {
        this.isTask = isTask;
    }

    // 执行下一个节点
    private BaseTask nextTask; // t2，t3，t4

    // 添加下一个节点任务
    public void addNextTask(BaseTask nextTask) {
        this.nextTask = nextTask;
    }

    // 让子节点任务去完成的
    public abstract void doAction();

    public void action() { // t1=false，t2=false，t3=true，
        if (isTask) { // t3
            doAction(); // 执行 子节点 链条断
        } else {
            // 继续执行下一个 任务节点
            if (nextTask != null) nextTask.action();
        }
    }
}
