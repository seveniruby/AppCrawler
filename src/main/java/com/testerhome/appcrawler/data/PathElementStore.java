package com.testerhome.appcrawler.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.Crawler;
import scala.App;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PathElementStore extends AbstractElementStore{

    private LinkedHashMap<String, AbstractElementInfo> linkedStore = new LinkedHashMap();
    List<AbstractElement> elementsList =new ArrayList<>();

    public Map<String, AbstractElementInfo> getStore() {
        return linkedStore;
    }

    public void setElementSkip(AbstractElement element) {
        if (!linkedStore.containsKey(element.elementUri())){
            linkedStore.put(element.elementUri(), AppCrawler.factory().generateElementInfo());
            linkedStore.get(element.elementUri()).setElement(element);
        }
        linkedStore.get(element.elementUri()).setAction(Status.SKIPPED);
    }

    public void setElementClicked(AbstractElement element) {
        if (!linkedStore.containsKey(element.elementUri())){
            linkedStore.put(element.elementUri(),AppCrawler.factory().generateElementInfo());
            linkedStore.get(element.elementUri()).setElement(element);
        }
        elementsList.add(element);
        linkedStore.get(element.elementUri()).setAction(Status.CLICKED);
        linkedStore.get(element.elementUri()).setClickedIndex(getClickElementList().indexOf(element));
        AppCrawler.crawler().domStore().saveDomUrl(element.elementUri());
    }

    public boolean isDiff() {
        return linkedStore.get(lastElementUri()).getReqHash()!=linkedStore.get(lastElementUri()).getResHash();
    }

    public boolean isClicked(AbstractElement element) {
        if (linkedStore.containsKey(element.elementUri())){
            return linkedStore.get(element.elementUri()).getAction()== Status.CLICKED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    //  isSkipped
    public boolean isSkipped(AbstractElement element) {
        if (linkedStore.containsKey(element.elementUri())){
            return linkedStore.get(element.elementUri()).getAction()== Status.SKIPPED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    public void saveElement(AbstractElement element) {
        if (!linkedStore.containsKey(element.elementUri())){
            linkedStore.put(element.elementUri(),AppCrawler.factory().generateElementInfo());
            linkedStore.get(element.elementUri()).setElement(element);
        }
    }

    public void saveReqHash(String hash) {
        if (linkedStore.get(lastElementUri()).getReqHash()==""){
            AppCrawler.log().info("save reqHash to "+(getClickElementList().size()-1));
            linkedStore.get(lastElementUri()).setReqHash(hash);
        }
    }

    public void saveResHash(String hash) {
        if (linkedStore.get(lastElementUri()).getResHash()==""){
            AppCrawler.log().info("save resHash to "+(getClickElementList().size()-1));
            linkedStore.get(lastElementUri()).setResHash(hash);
        }
    }

    public void saveReqDom(String dom) {
//        lastElementInfo().setReqDom(dom);
        System.out.println();
        AppCrawler.crawler().domStore().saveReqDom(lastElementUri(),dom);
        AppCrawler.log().info("save reqDom to "+(getClickElementList().size()-1));
    }

    public void saveResDom(String dom) {
        AppCrawler.crawler().domStore().saveResDom(lastElementUri(),dom);
        AppCrawler.log().info("save resDom to "+(getClickElementList().size()-1));
//        lastElementInfo().setResDom(dom);
    }

    public void saveReqImg(String imgName) {
        if (linkedStore.get(lastElementUri()).getReqImg()==""){
            AppCrawler.log().info("save reqImg " + imgName + "  to "+(getClickElementList().size()-1));
            linkedStore.get(lastElementUri()).setReqImg(imgName);
        }
    }

    public void saveResImg(String imgName) {
        if (linkedStore.get(lastElementUri()).getResImg()==""){
            AppCrawler.log().info("save resImg " + imgName + " to "+(getClickElementList().size()-1));
            linkedStore.get(lastElementUri()).setResImg(imgName);
        }
    }

    public String lastElementUri(){
        return elementsList.get(elementsList.size()-1).elementUri();
    }

    @JsonIgnore
    public List<AbstractElement> getClickElementList(){
        return elementsList;
    }
}
