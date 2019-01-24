package com.testerhome.appcrawler.data;

import java.util.List;
import java.util.Map;

public abstract class AbstractElementStore {
    public enum Status{
        READY,CLICKED,SKIPPED
    }

    public abstract boolean isClicked(AbstractElement element);
    public abstract boolean isSkipped(AbstractElement element);
    public abstract void saveElement(AbstractElement element);
    public abstract void setElementClicked(AbstractElement element);
    public abstract void setElementSkip(AbstractElement element);
    public abstract boolean isDiff();
    public abstract void saveReqHash(String hash);
    public abstract void saveResHash(String hash);
    public abstract void saveReqDom(String dom);
    public abstract void saveResDom(String dom);
    public abstract void saveReqImg(String imgName);
    public abstract void saveResImg(String imgName);
    public abstract List<AbstractElement> getClickElementList();
    public abstract Map<String,AbstractElementInfo> getStore();
}
