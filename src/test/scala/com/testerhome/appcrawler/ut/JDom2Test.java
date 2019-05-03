package com.testerhome.appcrawler.ut;


import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class JDom2Test {
    @Test
    void jdom2xpath() throws JDOMException, IOException {
        //todo: 使用这个代替默认的xpath实现
        //为了验证Uiautomator server的一段代码所写的用例
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(new File("/Users/seveniruby/temp/appcrawler/20190110160640_com.tencent.mobileqq/8_com.tencent.mobileqq.加好友- 管理员 -咕泡学院-Bob老师(2741426868).tag=Log.depth=2.id=Log.name=Log.dom"));
        System.out.println(jdomDocument);
        //Document jdomDocument = jdomBuilder.build(xmlSource);


        XPathFactory xFactory = XPathFactory.instance();
        // System.out.println(xFactory.getClass());

        // select all links
        XPathExpression<Attribute> expr = xFactory.compile("//android.widget.TextView/@text", Filters.attribute());
        List<Attribute> links = expr.evaluate(jdomDocument);
        System.out.println(links);
        for (Attribute linkElement : links) {
            System.out.println(linkElement.getName());
            System.out.println(linkElement.getValue());
            System.out.println(linkElement.getIntValue());
        }


    }
}
