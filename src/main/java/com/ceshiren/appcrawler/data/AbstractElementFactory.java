package com.ceshiren.appcrawler.data;

import java.util.Map;

public abstract class AbstractElementFactory {

    public abstract AbstractElementStore generateElementStore();
    public abstract AbstractElement generateElement();
    public abstract AbstractElement generateElement(String url, String tag, String id, String name,
                                                    String text, String instance, String depth, String valid,
                                                    String selected, String xpath, String ancestor,
                                                    int x, int y, int width, int height, String action);
    public abstract AbstractElement generateElement(Map map, String url);
    public abstract AbstractElementInfo generateElementInfo();

    public static AbstractElementFactory factorySwitch(boolean useNewData){
        return useNewData ? new PathElementFactory() : new URIElementFactory();
    }
}
