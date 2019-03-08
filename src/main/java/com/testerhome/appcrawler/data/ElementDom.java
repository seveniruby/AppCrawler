package com.testerhome.appcrawler.data;

public class ElementDom {

    private String reqDom;
    private String resDom;

    public ElementDom(String reqDom, String resDom) {
        this.reqDom = reqDom;
        this.resDom = resDom;
    }

    public void setReqDom(String reqDom) {
        this.reqDom = reqDom;
    }

    public void setResDom(String resDom) {
        this.resDom = resDom;
    }

    public String getReqDom() {
        return reqDom;
    }

    public String getResDom() {
        return resDom;
    }
}
