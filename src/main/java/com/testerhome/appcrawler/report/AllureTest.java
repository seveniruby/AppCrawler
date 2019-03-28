package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.AppCrawler;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@Feature("雪球app")
public class AllureTest {

    @Story("控件点击")
    @TestFactory
    Stream<DynamicTest> dynamicTestsExample() throws Exception {
        System.out.println("dynamicTestsExample run");

        List<DynamicTest> dynamicTests = new ArrayList<>();
        ReadYaml read = new ReadYaml();
        String path = AppCrawler.crawler().conf().resultDir();

        Map map = read.convert2Map(path + "/elements.yml");
        Map mapTitle = (HashMap) map.get("store");
        Map mapVal, mapEle;
        for(Object key : mapTitle.keySet()) {
            mapVal = (HashMap) mapTitle.get(key);
            Object req = mapVal.get("reqImg");
            Object res = mapVal.get("resImg");
            Object reqDom = mapVal.get("reqDom");
            Object resDom = mapVal.get("resDom");
            mapEle = (HashMap) mapVal.get("element");
            Object xpath = mapEle.get("xpath");
            Object url = mapEle.get("url");
            Object tag = mapEle.get("tag");
            Object id = mapEle.get("id");
            Object name = mapEle.get("name");
            Object text = mapEle.get("text");
            Object instance = mapEle.get("instance");
            Object depth = mapEle.get("depth");
            Object valid = mapEle.get("valid");
            Object selected = mapEle.get("selected");
            if(req == null || req.equals(""))
                req = "src/main/resources/404.png";
            if(res == null || res.equals(""))
                res = "src/main/resources/404.png";
            String reqImg = ""+req;
            String resImg = ""+res;
            dynamicTests.add(dynamicTest( "element(" + key + ")",
                    () ->  assertNotNull(addHtml(url.toString(), tag.toString(), id.toString(), name.toString(), text.toString(), instance.toString(), depth.toString(), valid.toString(), selected.toString())
                            + attributeAttachment(xpath.toString(), "xpath")
                            + lifeCycleAttach(reqImg.split("/")[reqImg.split("/").length -1], "image/png", ".png", new FileInputStream(reqImg))
                            + lifeCycleAttach(resImg.split("/")[resImg.split("/").length -1], "image/png", ".png", new FileInputStream(resImg))
                            + attributeAttachment(reqDom==null? "reqDom is null" : reqDom.toString().length() > 0 ? reqDom.toString() : "reqDom is null", "reqDom")
                            + attributeAttachment(resDom==null? "reqDom is null" : resDom.toString().length() > 0 ? resDom.toString() : "resDom is null", "resDom")
                            )));
        }
        return dynamicTests.stream();
    }
    public String addHtml(String url, String tag, String id, String name, String text, String instance, String depth, String valid, String selected){
        Allure.addDescriptionHtml("<table border='1'>" +
                "<tr><td colspan='2'>element属性</td></tr>" +
                "<tr><td>url</td><td>" + url + "</td></tr>" +
                "<tr><td>tag</td><td>" + tag + "</td></tr>" +
                "<tr><td>id</td><td>" + id + "</td></tr>" +
                "<tr><td>name</td><td>" + name + "</td></tr>" +
                "<tr><td>text</td><td>" + text + "</td></tr>" +
                "<tr><td>instance</td><td>" + instance + "</td></tr>" +
                "<tr><td>depth</td><td>" + depth + "</td></tr>" +
                "<tr><td>valid</td><td>" + valid + "</td></tr>" +
                "<tr><td>selected</td><td>" + selected + "</td></tr>" +
                "</table>");
        return null;
    }
    public String lifeCycleAttach(String name, String type, String extraName, FileInputStream path){
        Allure.getLifecycle().addAttachment(name, type, extraName, path);
        return null;
    }

    @Attachment
    public String attributeAttachment(String str, String name) throws Exception {
        modifyAnnotation("attributeAttachment", name, String.class, String.class);
        return str;
    }

    public void modifyAnnotation(String methodName, String attributeName, Class<?>... classes) throws Exception {
        Method method = AllureTest.class.getDeclaredMethod(methodName,classes);
        Attachment attachment = method.getAnnotation(Attachment.class);
        if (attachment == null)
            throw new RuntimeException("please add attachment");
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(attachment);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        memberValues.put("value", attributeName);
    }

}