package com.ceshiren.appcrawler.data;

import org.apache.commons.text.StringEscapeUtils;

import java.awt.*;
import java.io.File;
import java.util.Map;

public class PathElement extends AbstractElement {

    private String url;
    private String tag;
    private String id;
    private String name;
    private String text;
    private String instance;
    private String depth;
    private String valid;
    private String selected;
    private String xpath;
    private String ancestor;
    private int x;
    private int y;
    private int width;
    private int height;
    private String action;

    public PathElement(){
        this("","","","","","","","","","","",0,0,0,0,"");
    }
    public PathElement(String url, String tag, String id, String name, String text, String instance, String depth, String valid, String selected, String xpath, String ancestor, int x, int y, int width, int height, String action) {
        this.url = url;
        this.tag = tag;
        this.id = id;
        this.name = name;
        this.text = text;
        this.instance = instance;
        this.depth = depth;
        this.valid = valid;
        this.selected = selected;
        this.xpath = xpath;
        this.ancestor = ancestor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.action = action;
    }

    public PathElement(Map<String,Object> nodeMap, String uri){
        this.url=uri;
        this.tag = nodeMap.getOrDefault("name()", "").toString();
        this.id = nodeMap.getOrDefault("name", "").toString();
        this.name = nodeMap.getOrDefault("label", "").toString();
        this.text = nodeMap.getOrDefault("value", "").toString();
        this.instance = nodeMap.getOrDefault("instance", "").toString();
        this.depth = nodeMap.getOrDefault("depth", "").toString();
        this.xpath = nodeMap.getOrDefault("xpath", "").toString();
        this.x=Integer.valueOf(nodeMap.getOrDefault("x", "0").toString());
        this.y=Integer.valueOf(nodeMap.getOrDefault("y", "0").toString());
        this.width=Integer.valueOf(nodeMap.getOrDefault("width", "0").toString());
        this.height=Integer.valueOf(nodeMap.getOrDefault("height", "0").toString());
        this.ancestor=nodeMap.getOrDefault("ancestor", "").toString();
        this.selected=nodeMap.getOrDefault("selected", "false").toString();
        this.valid=nodeMap.getOrDefault("valid", "true").toString();
        this.action = nodeMap.getOrDefault("action", "").toString();
    }

    public Point center() {
        return new Point(x+width/2, y+height/2);
    }

    public Point location() {
        return new Point(x,y);
    }

    @Override
    public String elementUri() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(url.replace(File.separatorChar, '_'));
        fileName.append("." + validName());
        fileName.append("(" + tag.replace("android.widget.", "").replace("Activity", "")+")");

        return standardWinFileName(fileName.toString());
    }

    public String validName(){
        String validName = "";
        if(!text.isEmpty()){
            validName = StringEscapeUtils.unescapeHtml4(text).replace(File.separator, "+");
        }else if(!id.isEmpty()){
            int i = id.split("/").length;
            validName = id.split("/")[i-1];
        }else if(!name.isEmpty()){
            validName = name;
        }else{
            validName = tag.replace("android.widget.", "").replace("Activity", "");
        }
        return standardWinFileName(validName);
    }

    @Override
    public String toString() {
        return "url='" + url + '\'' +
                ", tag='" + tag + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", instance='" + instance + '\'' +
                ", depth='" + depth + '\'' +
                ", valid='" + valid + '\'' +
                ", selected='" + selected + '\'' +
                ", xpath='" + xpath + '\'' +
                ", ancestor='" + ancestor + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", action='" + action + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getAncestor() {
        return ancestor;
    }

    public void setAncestor(String ancestor) {
        this.ancestor = ancestor;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
