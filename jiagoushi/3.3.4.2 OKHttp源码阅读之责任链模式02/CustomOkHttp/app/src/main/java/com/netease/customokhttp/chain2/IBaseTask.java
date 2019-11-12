package com.netease.customokhttp.chain2;

public interface IBaseTask {

    /**
     * 参数一：任务节点是否有能力执行
     * 参数二：下一个任务节点
     * @param isTask
     */
    public void doRunAction(String isTask, IBaseTask iBaseTask);

}
