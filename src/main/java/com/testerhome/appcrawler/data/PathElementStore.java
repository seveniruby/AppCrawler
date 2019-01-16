package com.testerhome.appcrawler.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.URIElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PathElementStore {

    public enum Status{
        READY,CLICKED,SKIPPED
    }

    LinkedHashMap<String, AbstractElementInfo> linkedStore = new LinkedHashMap();

    List<String> resDomList = new ArrayList<>();

    public LinkedHashMap<String, AbstractElementInfo> getLinkedStore() {
        return linkedStore;
    }

    public void setElementSkip(AbstractElement element) {
        if (!linkedStore.containsKey(element.elementUri())){
            linkedStore.put(element.elementUri(),ElementFactory.newElementInfo());
            linkedStore.get(element.elementUri()).setElement(element);
        }
        linkedStore.get(element.elementUri()).setAction(Status.SKIPPED);
    }

    public void setElementClicked(AbstractElement element) {
        if (!linkedStore.containsKey(element.elementUri())){
            linkedStore.put(element.elementUri(),ElementFactory.newElementInfo());
            linkedStore.get(element.elementUri()).setElement(element);
        }
        linkedStore.get(element.elementUri()).setAction(Status.CLICKED);
        linkedStore.get(element.elementUri()).setClickedIndex(getClickElementList().indexOf(element));
    }

    public void setElementClear(AbstractElement element) {
        if (linkedStore.containsKey(element.elementUri())){
            linkedStore.remove(element.elementUri());
        }
    }

    public boolean isDiff() {
        return lastElementInfo().getReqHash()!=lastElementInfo().getResHash();
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
    public boolean isSkiped(AbstractElement element) {
        if (linkedStore.containsKey(element.elementUri())){
            return linkedStore.get(element.elementUri()).getAction()== Status.SKIPPED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    public void saveElement(AbstractElement element) {
        if (!linkedStore.containsKey(element.elementUri())){
            linkedStore.put(element.elementUri(),ElementFactory.newElementInfo());
            linkedStore.get(element.elementUri()).setElement(element);
        }
    }

    public void saveReqHash(String hash) {
        if (lastElementInfo().getReqHash()==""){
            AppCrawler.log().info("save reqHash to "+(linkedStore.size()-1));
            lastElementInfo().setReqHash(hash);
        }
    }

    public void saveResHash(String hash) {
        if (lastElementInfo().getResHash()==""){
            AppCrawler.log().info("save resHash to "+(linkedStore.size()-1));
            lastElementInfo().setResHash(hash);
        }
    }

    public void saveReqDom(String dom) {
        if (linkedStore.size()<2){
            lastElementInfo().setReqDom(dom);
        }else{
            lastElementInfo().setReqDom(resDomList.get(resDomList.size() - 1));
        }
        AppCrawler.log().info("save reqDom to "+(linkedStore.size()-1));
    }

    public void saveResDom(String dom) {
        AppCrawler.log().info("save resDom to "+(linkedStore.size()-1));
        lastElementInfo().setResDom(dom);
        resDomList.add(dom);
    }

    public void saveReqImg(String imgName) {
        if (lastElementInfo().getReqImg()==""){
            AppCrawler.log().info("save reqImg " + imgName + "  to "+(linkedStore.size()-1));
            lastElementInfo().setReqImg(imgName);
        }
    }

    public void saveResImg(String imgName) {
        if (lastElementInfo().getResImg()==""){
            AppCrawler.log().info("save resImg " + imgName + " to "+(linkedStore.size()-1));
            lastElementInfo().setResImg(imgName);
        }
    }

    public void setLinkedStore(LinkedHashMap<String, AbstractElementInfo> linkedStore) {
        this.linkedStore = linkedStore;
    }

    // 获取map中获取最后一个控件的信息
    public AbstractElementInfo lastElementInfo(){
        int size = getClickElementList().size();
        if (size>0){
            return linkedStore.get(getClickElementList().get(size-1).elementUri());
        }else {
            return null;
        }
    }

    // 获取map列表中点击控件的列表
    @JsonIgnore
    public List<AbstractElement> getClickElementList(){
        List<AbstractElement> list = new ArrayList<>();
        for (String key : linkedStore.keySet()){
            if (linkedStore.get(key).getAction()== Status.CLICKED){
                list.add(linkedStore.get(key).getElement());
            }
        }
        return list;
    }
}
