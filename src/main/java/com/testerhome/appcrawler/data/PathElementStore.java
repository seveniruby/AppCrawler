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

    LinkedHashMap<String, PathElementInfo> linkedStore = new LinkedHashMap();

    List<String> resDomList = new ArrayList<>();

    public LinkedHashMap<String, PathElementInfo> getLinkedStore() {
        return linkedStore;
    }

    public void setElementSkip(URIElement element) {
        if (!linkedStore.containsKey(element.toString())){
            linkedStore.put(element.toString(),new PathElementInfo());
            linkedStore.get(element.toString()).setUriElement(element);
        }
        linkedStore.get(element.toString()).setAction(Status.SKIPPED);
    }

    public void setElementClicked(URIElement element) {
        if (!linkedStore.containsKey(element.toString())){
            linkedStore.put(element.toString(),new PathElementInfo());
            linkedStore.get(element.toString()).setUriElement(element);
        }
        linkedStore.get(element.toString()).setAction(Status.CLICKED);
        linkedStore.get(element.toString()).setClickedIndex(getClickElementList().indexOf(element));
    }

    public void setElementClear(URIElement element) {
        if (linkedStore.containsKey(element.toString())){
            linkedStore.remove(element.toString());
        }
    }

    public boolean isDiff() {
        return lastElementInfo().getReqHash()!=lastElementInfo().getResHash();
    }

    public boolean isClicked(URIElement element) {
        if (linkedStore.containsKey(element.toString())){
            return linkedStore.get(element.toString()).getAction()== Status.CLICKED;
        }else {
            AppCrawler.log().info("element="+element+"first show, need click");
            return false;
        }
    }

    //  isSkipped
    public boolean isSkiped(URIElement element) {
        if (linkedStore.containsKey(element.toString())){
            return linkedStore.get(element.toString()).getAction()== Status.SKIPPED;
        }else {
            AppCrawler.log().info("element="+element+"first show, need click");
            return false;
        }
    }

    public void saveElement(URIElement element) {
        if (!linkedStore.containsKey(element.toString())){
            linkedStore.put(element.toString(),new PathElementInfo());
            linkedStore.get(element.toString()).setUriElement(element);
        }
    }

    public void saveReqHash(String hash) {
        if (lastElementInfo().getReqHash()==null){
            AppCrawler.log().info("save reqHash to "+(linkedStore.size()-1));
            lastElementInfo().setReqHash(hash);
        }
    }

    public void saveResHash(String hash) {
        if (lastElementInfo().getResHash()==null){
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
        if (lastElementInfo().getReqImg()==null){
            AppCrawler.log().info("save reqImg " + imgName + "  to "+(linkedStore.size()-1));
            lastElementInfo().setReqImg(imgName);
        }
    }

    public void saveResImg(String imgName) {
        if (lastElementInfo().getResImg()==null){
            AppCrawler.log().info("save resImg " + imgName + " to "+(linkedStore.size()-1));
            lastElementInfo().setResImg(imgName);
        }
    }

    // 获取map中获取最后一个控件的信息
    public PathElementInfo lastElementInfo(){
        return linkedStore.get(getClickElementList().get(getClickElementList().size()-1).toString());
    }

    // 获取map列表中点击控件的列表
    @JsonIgnore
    public List<URIElement> getClickElementList(){
        List<URIElement> list = new ArrayList<>();
        for (String key : linkedStore.keySet()){
            if (linkedStore.get(key).action== Status.CLICKED){
                list.add(linkedStore.get(key).uriElement);
            }
        }
        return list;
    }
}
