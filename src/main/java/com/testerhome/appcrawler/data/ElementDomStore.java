package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.AppCrawler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ElementDomStore {

    private LinkedHashMap<String, ElementDom> domStore = new LinkedHashMap();
    public Map<String, ElementDom> getDomStore() {
        return domStore;
    }

    public void saveDomUrl(String url){
        if (!domStore.containsKey(url)){
            domStore.put(url,new ElementDom("",""));
        }
    }

    public void saveReqDom(String url, String dom) {
        if (domStore.containsKey(url)){
            domStore.get(url).setReqDom(dom);
        }
    }

    public void saveResDom(String url,String dom) {
        if (domStore.containsKey(url)){
            domStore.get(url).setResDom(dom);
        }
    }

    public void clearDom(){
        domStore.clear();
    }
}
