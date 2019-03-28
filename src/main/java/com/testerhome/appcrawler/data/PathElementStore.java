package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.AppCrawler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PathElementStore extends AbstractElementStore{

    private LinkedHashMap<String, AbstractElementInfo> elementStore = new LinkedHashMap();
    private List<AbstractElement> clickedElementsList =new ArrayList<>();

    public Map<String, AbstractElementInfo> storeMap() {
        return elementStore;
    }

    public LinkedHashMap<String, AbstractElementInfo> getElementStore() {
        return elementStore;
    }

    public List<AbstractElement> getClickedElementsList() {
        return clickedElementsList;
    }

    public void setElementSkip(AbstractElement element) {
        if (!elementStore.containsKey(element.elementUri())){
            elementStore.put(element.elementUri(), AppCrawler.factory().generateElementInfo());
            elementStore.get(element.elementUri()).setElement(element);
        }
        elementStore.get(element.elementUri()).setAction(Status.SKIPPED);
    }

    public void setElementClicked(AbstractElement element) {
        if (!elementStore.containsKey(element.elementUri())){
            elementStore.put(element.elementUri(),AppCrawler.factory().generateElementInfo());
            elementStore.get(element.elementUri()).setElement(element);
        }
        clickedElementsList.add(element);
        elementStore.get(element.elementUri()).setAction(Status.CLICKED);
        elementStore.get(element.elementUri()).setClickedIndex(clickElementList().indexOf(element));
    }

    public boolean isDiff() {
        return elementStore.get(lastElementUri()).getReqHash()!= elementStore.get(lastElementUri()).getResHash();
    }

    public boolean isClicked(AbstractElement element) {
        if (elementStore.containsKey(element.elementUri())){
            return elementStore.get(element.elementUri()).getAction()== Status.CLICKED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    //  isSkipped
    public boolean isSkipped(AbstractElement element) {
        if (elementStore.containsKey(element.elementUri())){
            return elementStore.get(element.elementUri()).getAction()== Status.SKIPPED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    public void saveElement(AbstractElement element) {
        if (!elementStore.containsKey(element.elementUri())){
            elementStore.put(element.elementUri(),AppCrawler.factory().generateElementInfo());
            elementStore.get(element.elementUri()).setElement(element);
        }
    }

    public void saveReqHash(String hash) {
        if (elementStore.get(lastElementUri()).getReqHash()==""){
            AppCrawler.log().info("save reqHash to "+(clickElementList().size()-1));
            elementStore.get(lastElementUri()).setReqHash(hash);
        }
    }

    public void saveResHash(String hash) {
        if (elementStore.get(lastElementUri()).getResHash()==""){
            AppCrawler.log().info("save resHash to "+(clickElementList().size()-1));
            elementStore.get(lastElementUri()).setResHash(hash);
        }
    }

    public void saveReqDom(String dom) {
        AppCrawler.log().info("save reqDom to "+(clickElementList().size()-1));
        elementStore.get(lastElementUri()).setReqDom(dom);
    }

    public void saveResDom(String dom) {
        AppCrawler.log().info("save resDom to "+(clickElementList().size()-1));
        elementStore.get(lastElementUri()).setResDom(dom);
    }

    public void saveReqImg(String imgName) {
        if (elementStore.get(lastElementUri()).getReqImg()==""){
            AppCrawler.log().info("save reqImg " + imgName + "  to "+(clickElementList().size()-1));
            elementStore.get(lastElementUri()).setReqImg(imgName);
        }
    }

    public void saveResImg(String imgName) {
        if (elementStore.get(lastElementUri()).getResImg()==""){
            AppCrawler.log().info("save resImg " + imgName + " to "+(clickElementList().size()-1));
            elementStore.get(lastElementUri()).setResImg(imgName);
        }
    }

    public String lastElementUri(){
        return clickedElementsList.get(clickedElementsList.size()-1).elementUri();
    }

    public List<AbstractElement> clickElementList(){
        return clickedElementsList;
    }
}
