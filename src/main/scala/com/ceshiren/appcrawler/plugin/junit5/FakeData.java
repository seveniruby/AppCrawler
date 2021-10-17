package com.ceshiren.appcrawler.plugin.junit5;

import scala.collection.mutable.HashMap;

public class FakeData {
    HashMap<String, Object> map=new HashMap<>();
    public FakeData(){
        map.put("Page1", 1);
        map.put("Page2", 2);
        map.put("Page3", 3);
        map.put("Page4", 4);
    }
}
