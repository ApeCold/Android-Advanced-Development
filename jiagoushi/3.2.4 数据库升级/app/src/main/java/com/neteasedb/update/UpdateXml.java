package com.neteasedb.update;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

// xml文件根节点
public class UpdateXml {

    private List<UpdateStep> updateSteps;

    public UpdateXml(Document document){
        // 获取升级得脚本，解析根节点
        NodeList updateSteps = document.getElementsByTagName("updateStep");
        this.updateSteps = new ArrayList<>();
        for(int i = 0; i< updateSteps.getLength();i++){
            Element ele = (Element) updateSteps.item(i);
            UpdateStep step = new UpdateStep(ele);
            this.updateSteps.add(step);
        }
    }

    public List<UpdateStep> getUpdateSteps() {
        return updateSteps;
    }

}
