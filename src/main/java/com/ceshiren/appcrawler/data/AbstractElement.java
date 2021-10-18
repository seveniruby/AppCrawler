package com.ceshiren.appcrawler.data;

import com.ceshiren.appcrawler.URIElement;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonDeserialize(as = URIElement.class)
public abstract class AbstractElement {
    public abstract Point center();
    public abstract String getId();
    public abstract String getName();
    public abstract String getTag();
    public abstract String getText();
    public abstract String getInstance();
    //todo: adb driver need fix url with package/.activity
    public abstract String getUrl();
    public abstract String getXpath();
    public abstract String getAction();
    public abstract String getAncestor();
    public abstract String elementUri();
    public abstract void setId(String id);
    public abstract void setName(String name);
    public abstract void setTag(String tag);
    public abstract void setAction(String action);
    public abstract String getValid();
    public abstract int getX();
    public abstract int getY();
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract String getDepth();
    public abstract String getSelected();
    // windows下命名规范
    public String standardWinFileName(String s){
        // a-z  A-Z 0-9 _ 汉字
        String regex="[^a-zA-Z0-9.=()_\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(regex);
        Matcher match=pattern.matcher(s);
        return match.replaceAll("");
    }
}
