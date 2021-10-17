package com.ceshiren.appcrawler.data;

public abstract class AbstractElementInfo {
    public abstract AbstractElement getElement();
    public abstract void setElement(AbstractElement element);
    public abstract void setAction(AbstractElementStore.Status status);
    public abstract void setClickedIndex(int index);
    public abstract int getClickedIndex();
    public abstract AbstractElementStore.Status getAction();
    public abstract void setReqHash(String reqHash);
    public abstract void setResHash(String resHash);
    public abstract void setReqDom(String reqDom);
    public abstract void setResDom(String resDom);
    public abstract void setReqImg(String reqImg);
    public abstract void setResImg(String resImg);
    public abstract void setReqTime(String reqTime);
    public abstract void setResTime(String resTime);
    public abstract String getReqHash();
    public abstract String getReqDom();
    public abstract String getReqImg();
    public abstract String getResHash();
    public abstract String getResDom();
    public abstract String getResImg();
    public abstract String getReqTime();
    public abstract String getResTime();
}
