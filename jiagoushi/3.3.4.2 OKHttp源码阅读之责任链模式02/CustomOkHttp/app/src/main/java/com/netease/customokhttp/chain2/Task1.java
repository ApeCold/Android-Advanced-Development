package com.netease.customokhttp.chain2;

public class Task1 implements IBaseTask {

    @Override
    public void doRunAction(String isTask, IBaseTask iBaseTask) { // iBaseTask == ChainManager
        if ("no".equals(isTask)) {
            System.out.println("拦截器 任务节点一 处理了...");
            return;
        } else {
            // 继续执行下一个链条的任务节点  ChainManager.doRunAction("ok", ChainManager)
            // ChainManager.doRunAction
            iBaseTask.doRunAction(isTask, iBaseTask);
        }
    }
}
