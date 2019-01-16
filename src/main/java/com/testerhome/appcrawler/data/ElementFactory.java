package com.testerhome.appcrawler.data;

import com.testerhome.appcrawler.CrawlerConf;
import com.testerhome.appcrawler.ElementInfo;
import com.testerhome.appcrawler.URIElement;
import scala.collection.JavaConverters;

import java.util.Map;

public class ElementFactory {
    static CrawlerConf conf = new CrawlerConf();
    public static AbstractElement newElement(){
        if (conf.useNewData()){
            return new PathElement();
        }else {
            return new URIElement();
        }
    }
    public static AbstractElement newElement(String url, String tag, String id, String name, String text, String instance, String depth, String valid, String selected, String xpath, String ancestor, int x, int y, int width, int height, String action){
        if (conf.useNewData()){
            return new PathElement(url,tag,id,name,text,instance,depth,valid,selected,xpath,ancestor,x,y,width,height,action);
        }else {
            return new URIElement(url,tag,id,name,text,instance,depth,valid,selected,xpath,ancestor,x,y,width,height,action);
        }
    }
    public static AbstractElement newElement(Map map, String url){
        if (conf.useNewData()){
            return new PathElement(map,url);
        }else {
            scala.collection.Map nodeMap = JavaConverters.mapAsScalaMap(map);
            return new URIElement(nodeMap,url);
        }
    }

    public static AbstractElementInfo newElementInfo(){
        if (conf.useNewData()){
            return new PathElementInfo();
        }else {
            return new ElementInfo();
        }
    }

//    public static Point center(AbstractElement element){
//        return element.center();
//    }
//
//    public static int getHeight(AbstractElement element){
//        return element.getHeight();
//    }
//    public static int getWidth(AbstractElement element){
//        return element.getWidth();
//    }
//    public static int getX(AbstractElement element){
//        return element.getX();
//    }
//    public static int getY(AbstractElement element){
//        return element.getY();
//    }
//
//    public static String getId(AbstractElement element){
//        return element.getId();
//    }
//
//    public static String getName(AbstractElement element){
//        return element.getName();
//    }
//
//    public static String getTag(AbstractElement element){
//        return element.getTag();
//    }
//
//    public static String getText(AbstractElement element){
//        return element.getText();
//    }
//
//    public static String getInstance(AbstractElement element){
//        return element.getInstance();
//    }
//
//    public static String getXpath(AbstractElement element){
//        return element.getXpath();
//    }
//
//    public static String getUrl(AbstractElement element){
//        return element.getUrl();
//    }
//
//    public static String getAction(AbstractElement element){
//        return element.getAction();
//    }
//
//    public static void setAction(AbstractElement element,String action){
//        element.setAction(action);
//    }
//
//    public static String getAncestor(AbstractElement element){
//        return element.getAncestor();
//    }
}
