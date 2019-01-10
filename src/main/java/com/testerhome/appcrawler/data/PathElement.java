package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.URIElement;
import scala.collection.Map;

import java.io.File;

public class PathElement extends URIElement {

    public PathElement(String url, String tag, String id,
                       String name, String text, String instance,
                       String depth, String valid, String selected,
                       String xpath, String ancestor,
                       int x, int y, int width, int height, String action) {
        super(url, tag, id, name, text, instance, depth, valid,
                selected, xpath, ancestor, x, y, width, height, action);
    }

    public PathElement(Map<String, Object> nodeMap, String uri) {
        super(nodeMap, uri);
    }

    public String getElementUrl(){
        StringBuilder sb = new StringBuilder();
        sb.append(url().replace(File.separatorChar,'_'));
        sb.append("." + text());
        sb.append("&tag=" + tag());
        sb.append("&id=" + id());
        sb.append("&depth=" + depth());
        return sb.toString();
    }
}
