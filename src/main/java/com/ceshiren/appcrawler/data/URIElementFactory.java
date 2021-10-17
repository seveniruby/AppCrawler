package com.ceshiren.appcrawler.data;

import com.ceshiren.appcrawler.ElementInfo;
import com.ceshiren.appcrawler.URIElement;
import com.ceshiren.appcrawler.URIElementStore;
import scala.collection.JavaConverters;

import java.util.Map;

public class URIElementFactory extends AbstractElementFactory {
    @Override
    public AbstractElementStore generateElementStore() {
        return new URIElementStore();
    }

    @Override
    public AbstractElement generateElement() {
        return new URIElement();
    }

    @Override
    public AbstractElement generateElement(String url, String tag, String id, String name, String text,
                                           String instance, String depth, String valid, String selected,
                                           String xpath, String ancestor, int x, int y, int width, int height,
                                           String action) {
        return new URIElement(url, tag, id, name, text, instance, depth, valid, selected,
                                xpath, ancestor, x, y, width, height, action);
    }

    @Override
    public AbstractElement generateElement(Map map, String url) {
        scala.collection.Map nodeMap = JavaConverters.mapAsScalaMap(map);
        return new URIElement(nodeMap, url);
    }

    @Override
    public AbstractElementInfo generateElementInfo() {
        return new ElementInfo();
    }
}
