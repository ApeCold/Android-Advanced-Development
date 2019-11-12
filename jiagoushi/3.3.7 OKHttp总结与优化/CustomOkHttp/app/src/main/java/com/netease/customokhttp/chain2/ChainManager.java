package com.netease.customokhttp.chain2;

import java.util.ArrayList;
import java.util.List;

public class ChainManager implements IBaseTask {

    private List<IBaseTask> iBaseTaskList = new ArrayList<>();

    public void addTask(IBaseTask iBaseTask) {
        iBaseTaskList.add(iBaseTask);
    }

    private int index = 0;

    @Override
    public void doRunAction(String isTask, IBaseTask iBaseTask) {
        if (iBaseTaskList.isEmpty()) {
            // 抛出异常..
            return;
        }

        if (index == iBaseTaskList.size() || index > iBaseTaskList.size()) {
            return;
        }

        IBaseTask iBaseTaskResult = iBaseTaskList.get(index); // index 0 t1,    index 1 t2      index 2 t3

        index ++;

        // iBaseTaskResult本质==Task1，   iBaseTaskResult本质==Task2      iBaseTaskResult本质==Task3
        iBaseTaskResult.doRunAction(isTask, iBaseTask);


    }
}
