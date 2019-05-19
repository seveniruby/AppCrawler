package com.testerhome.appcrawler.data;

import java.util.Map;

public class PathElementFactory extends AbstractElementFactory {
    @Override
    public AbstractElementStore generateElementStore() {
        return new PathElementStore();
    }

    @Override
    public AbstractElement generateElement() {
        return new PathElement();
    }

    @Override
    public AbstractElement generateElement(String url, String tag, String id, String name, String text,
                                           String instance, String depth, String valid, String selected, String xpath,
                                           String ancestor, int x, int y, int width, int height, String action) {
        return new PathElement(url, tag, id, name, text, instance, depth, valid, selected, xpath,
                                ancestor, x, y, width, height, action);
    }

    @Override
    public AbstractElement generateElement(Map map, String url) {
        return new PathElement(map, url);
    }

    @Override
    public AbstractElementInfo generateElementInfo() {
        return new PathElementInfo();
    }
}
