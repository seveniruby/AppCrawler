package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.TData;

import java.io.Serializable;
import java.util.*;

public class SimpleElementStore extends AbstractElementStore {

    private LinkedHashMap<String, AbstractElementInfo> elementStore = new LinkedHashMap();
    private List<AbstractElement> clickedElementsList =new ArrayList<>();
    private Map<String, String> domInfoMap = new HashMap<>();

    @Override
    public LinkedHashMap<String, AbstractElementInfo> getElementStoreMap() {
        return elementStore;
    }
    @Override
    public List<AbstractElement> getClickedElementsList() {
        return clickedElementsList;
    }
    public Map<String,String> getDomInfoMap(){
        return domInfoMap;
    }

    @Override
    public boolean isClicked(AbstractElement element) {
        if (elementStore.containsKey(element.elementUri())){
            return elementStore.get(element.elementUri()).getAction()== Status.CLICKED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    @Override
    public boolean isSkipped(AbstractElement element) {
        if (elementStore.containsKey(element.elementUri())){
            return elementStore.get(element.elementUri()).getAction()== Status.SKIPPED;
        }else {
            AppCrawler.log().info("element="+element.elementUri()+"first show, need click");
            return false;
        }
    }

    @Override
    public void saveElement(AbstractElement element) {
        if (!elementStore.containsKey(element.elementUri())){
            elementStore.put(element.elementUri(),AppCrawler.factory().generateElementInfo());
        }
    }

    @Override
    public void setElementClicked(AbstractElement element) {
        if (!elementStore.containsKey(element.elementUri())){
            elementStore.put(element.elementUri(),AppCrawler.factory().generateElementInfo());
        }
        clickedElementsList.add(element);
        elementStore.get(element.elementUri()).setElement(null);
        elementStore.get(element.elementUri()).setAction(Status.CLICKED);
        elementStore.get(element.elementUri()).setClickedIndex(this.getClickedElementsList().indexOf(element) + 1);
    }

    @Override
    public void setElementSkip(AbstractElement element) {
        if (!elementStore.containsKey(element.elementUri())){
            elementStore.put(element.elementUri(), AppCrawler.factory().generateElementInfo());
        }
        elementStore.get(element.elementUri()).setElement(element);
        elementStore.get(element.elementUri()).setAction(Status.SKIPPED);
    }

    @Override
    public boolean isDiff() {
        return elementStore.get(lastElementUri()).getReqHash()!= elementStore.get(lastElementUri()).getResHash();
    }

    @Override
    public void saveReqHash(String hash) {
        if (elementStore.get(lastElementUri()).getReqHash()==""){
            AppCrawler.log().info("save reqHash to "+(this.getClickedElementsList().size()-1));
            elementStore.get(lastElementUri()).setReqHash(hash);
        }
    }

    @Override
    public void saveResHash(String hash) {
        if (elementStore.get(lastElementUri()).getResHash()==""){
            AppCrawler.log().info("save resHash to "+(this.getClickedElementsList().size()-1));
            elementStore.get(lastElementUri()).setResHash(hash);
        }
    }

    @Override
    public void saveReqDom(String dom) {
        String reqDomMd5 = TData.md5(1, dom);
        if (!domInfoMap.containsKey(reqDomMd5)){
            domInfoMap.put(reqDomMd5, dom);
        }
        AppCrawler.log().info("save reqDom to "+(this.getClickedElementsList().size()-1));

        // 存储dom的md5索引
        elementStore.get(lastElementUri()).setReqDom(reqDomMd5);
    }

    @Override
    public void saveResDom(String dom) {
        String resDomMd5 = TData.md5(1, dom);
        if (!domInfoMap.containsKey(resDomMd5)){
            domInfoMap.put(resDomMd5, dom);
        }
        AppCrawler.log().info("save resDom to "+(this.getClickedElementsList().size()-1));
        elementStore.get(lastElementUri()).setResDom(resDomMd5);
    }

    @Override
    public void saveReqImg(String imgName) {
        if (elementStore.get(lastElementUri()).getReqImg()==""){
            AppCrawler.log().info("save reqImg " + imgName + "  to "+(this.getClickedElementsList().size()-1));
            elementStore.get(lastElementUri()).setReqImg(imgName);
        }
    }

    @Override
    public void saveResImg(String imgName) {
        if (elementStore.get(lastElementUri()).getResImg()==""){
            AppCrawler.log().info("save resImg " + imgName + " to "+(this.getClickedElementsList().size()-1));
            elementStore.get(lastElementUri()).setResImg(imgName);
        }
    }
    @Override
    public void saveReqTime(String reqTime){
        if (elementStore.get(lastElementUri()).getReqTime()==""){
            AppCrawler.log().info("save reqTime " + reqTime + " to "+(this.getClickedElementsList().size()-1));
            elementStore.get(lastElementUri()).setReqTime(reqTime);
        }
    }
    @Override
    public void saveResTime(String resTime){
        if (elementStore.get(lastElementUri()).getResTime()==""){
            AppCrawler.log().info("save resTime " + resTime + " to "+(this.getClickedElementsList().size()-1));
            elementStore.get(lastElementUri()).setResTime(resTime);
        }
    }

    private String lastElementUri(){
        return clickedElementsList.get(clickedElementsList.size()-1).elementUri();
    }

    // 反序列化后首先调用该方法更新ElementInfo数据后，调用info.getReqDom取数据
    public void updateInfoData(){
        for (String key : elementStore.keySet()){
            AbstractElementInfo info = elementStore.get(key);
            String reqDomKey = info.getReqDom();
            String resDomKey = info.getResDom();
            info.setReqDom(domInfoMap.get(reqDomKey));
            info.setResDom(domInfoMap.get(resDomKey));
            info.setElement(clickedElementsList.get(info.getClickedIndex()));
        }
    }
}
