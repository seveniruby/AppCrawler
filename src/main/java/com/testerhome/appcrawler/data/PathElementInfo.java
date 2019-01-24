package com.testerhome.appcrawler.data;

public class PathElementInfo extends AbstractElementInfo{

    String reqDom;
    String resDom;
    String reqHash;
    String resHash;
    String reqImg;
    String resImg;
    AbstractElementStore.Status action;
    int clickedIndex;
    AbstractElement uriElement;

    public PathElementInfo(){
        this("","","","","","", AbstractElementStore.Status.READY,-1,ElementFactory.newElement());
    }
    public PathElementInfo(String reqDom, String resDom, String reqHash, String resHash, String reqImg, String resImg, AbstractElementStore.Status action, int clickedIndex, AbstractElement uriElement) {
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

    public void setAction(AbstractElementStore.Status action) {
        this.action = action;
    }

    public void setElement(AbstractElement uriElement) {
        this.uriElement = uriElement;
    }

    public AbstractElementStore.Status getAction() {
        return action;
    }

    public AbstractElement getElement() {
        return uriElement;
    }

    public int getClickedIndex() {
        return clickedIndex;
    }

    public void setClickedIndex(int clickedIndex) {
        this.clickedIndex = clickedIndex;
    }
}
