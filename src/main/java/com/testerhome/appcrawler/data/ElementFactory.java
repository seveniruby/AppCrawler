package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.*;
import scala.collection.JavaConverters;

import java.util.Map;

public class ElementFactory {

    private boolean useNewData = false;

    public void setSwitch(boolean useNewData) {
        this.useNewData = useNewData;
    }

    public AbstractElementStore generateElementStore() {
        return useNewData ? new PathElementStore() : new URIElementStore();
    }

    public AbstractElement generateElement() {
        return useNewData ? new PathElement() : new URIElement();
    }

    public AbstractElement generateElement(String url, String tag, String id, String name, String text, String instance, String depth, String valid, String selected, String xpath, String ancestor, int x, int y, int width, int height, String action) {
        return useNewData ?
                new PathElement(url, tag, id, name, text, instance, depth, valid, selected, xpath, ancestor, x, y, width, height, action)
                : new URIElement(url, tag, id, name, text, instance, depth, valid, selected, xpath, ancestor, x, y, width, height, action);
    }

    public AbstractElement generateElement(Map map, String url) {
        scala.collection.Map nodeMap = JavaConverters.mapAsScalaMap(map);
        return useNewData ? new PathElement(map, url) : new URIElement(nodeMap, url);
    }

    public AbstractElementInfo generateElementInfo() {
        return useNewData ? new PathElementInfo() : new ElementInfo();
    }
}