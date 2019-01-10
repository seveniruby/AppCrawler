package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.URIElement;

public class PathElementInfo {

    String reqDom;
    String resDom;
    String reqHash;
    String resHash;
    String reqImg;
    String resImg;
    PathElementStore.Status action = PathElementStore.Status.READY;
    int clickedIndex;
    URIElement uriElement;

    public PathElementInfo(){}

    public String getReqDom() {
        return reqDom;
    }

    public void setReqDom(String reqDom) {
        this.reqDom = reqDom;
    }

    public String getResDom() {
        return resDom;
    }

    public void setResDom(String resDom) {
        this.resDom = resDom;
    }

    public String getReqHash() {
        return reqHash;
    }

    public void setReqHash(String reqHash) {
        this.reqHash = reqHash;
    }

    public String getResHash() {
        return resHash;
    }

    public void setResHash(String resHash) {
        this.resHash = resHash;
    }

    public String getReqImg() {
        return reqImg;
    }

    public void setReqImg(String reqImg) {
        this.reqImg = reqImg;
    }

    public String getResImg() {
        return resImg;
    }

    public void setResImg(String resImg) {
        this.resImg = resImg;
    }

    public void setAction(PathElementStore.Status action) {
        this.action = action;
    }

    public void setUriElement(URIElement uriElement) {
        this.uriElement = uriElement;
    }

    public PathElementStore.Status getAction() {
        return action;
    }

    public URIElement getUriElement() {
        return uriElement;
    }

    public int getClickedIndex() {
        return clickedIndex;
    }

    public void setClickedIndex(int clickedIndex) {
        this.clickedIndex = clickedIndex;
    }
}
