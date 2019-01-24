package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.*;
import scala.collection.JavaConverters;

import java.util.Map;

public class ElementFactory {
    // todo : 初始化的问题
    static boolean useNewData = true;

    public static AbstractElementStore newElementStore() {
        return useNewData ? new PathElementStore() : new URIElementStore();
    }

    public static AbstractElement newElement() {
        return useNewData ? new PathElement() : new URIElement();
    }

    public static AbstractElement newElement(String url, String tag, String id, String name, String text, String instance, String depth, String valid, String selected, String xpath, String ancestor, int x, int y, int width, int height, String action) {
        return useNewData ?
                new PathElement(url, tag, id, name, text, instance, depth, valid, selected, xpath, ancestor, x, y, width, height, action)
                : new URIElement(url, tag, id, name, text, instance, depth, valid, selected, xpath, ancestor, x, y, width, height, action);
    }

    public static AbstractElement newElement(Map map, String url) {
        scala.collection.Map nodeMap = JavaConverters.mapAsScalaMap(map);
        return useNewData ? new PathElement(map, url) : new URIElement(nodeMap, url);
    }

    public static AbstractElementInfo newElementInfo() {
        return useNewData ? new PathElementInfo() : new ElementInfo();
    }
}