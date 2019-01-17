package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.URIElement;

public class PathElementInfo extends AbstractElementInfo{

    String reqDom;
    String resDom;
    String reqHash;
    String resHash;
    String reqImg;
    String resImg;
    PathElementStore.Status action;
    int clickedIndex;
    PathElement uriElement;

    public PathElementInfo(){
        this("","","","","","",PathElementStore.Status.READY,-1,null);
    }
    public PathElementInfo(String reqDom, String resDom, String reqHash, String resHash, String reqImg, String resImg, PathElementStore.Status action, int clickedIndex, PathElement uriElement) {
        this.reqDom = reqDom;
        this.resDom = resDom;
        this.reqHash = reqHash;
        this.resHash = resHash;
        this.reqImg = reqImg;
        this.resImg = resImg;
        this.action = action;
        this.clickedIndex = clickedIndex;
        this.uriElement = uriElement;
    }

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

    public void setElement(AbstractElement uriElement) {
        this.uriElement = (PathElement)uriElement;
    }

    public PathElementStore.Status getAction() {
        return action;
    }

    public PathElement getElement() {
        return uriElement;
    }

    public int getClickedIndex() {
        return clickedIndex;
    }

    public void setClickedIndex(int clickedIndex) {
        this.clickedIndex = clickedIndex;
    }
}
