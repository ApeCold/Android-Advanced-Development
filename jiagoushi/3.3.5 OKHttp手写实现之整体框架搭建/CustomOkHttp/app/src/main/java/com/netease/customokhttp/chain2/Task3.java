package com.netease.customokhttp.chain2;

public class Task3 implements IBaseTask {

    @Override
    public void doRunAction(String isTask, IBaseTask iBaseTask) {
        if ("no".equals(isTask)) {
            System.out.println("拦截器 任务节点三 处理了...");
            return;
        } else {
            // 继续执行下一个链条的任务节点
            iBaseTask.doRunAction(isTask, iBaseTask);
        }
    }
}
