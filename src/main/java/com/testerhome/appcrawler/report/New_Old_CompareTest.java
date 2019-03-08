package com.testerhome.appcrawler.report;

import io.qameta.allure.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
@Epic("新老版本对比")
public class New_Old_CompareTest {

    @Feature("当前页面")
    @TestFactory
    Stream<DynamicTest> dynamicTestsExample() throws Exception {
        List<DynamicTest> dynamicTests = new ArrayList<>();
        ReadYaml read = new ReadYaml();
        Map mapRoot = read.convert2Map("./src/test/yaml/diff.yml");
        //System.out.println(mapRoot);

        for(Object key : mapRoot.keySet()) {
            //System.out.println(key + ":" + mapRoot.get(key));
            //DynamicTest testPage = dynamicTest(key.toString(), () -> assertNotNull(""));
            Map mapBtn = (HashMap)mapRoot.get(key);
            for(Object keyBtn : mapBtn.keySet()){
                //System.out.println(keyBtn + ":" + mapBtn.get(keyBtn));
                Map mapBtnDetail = (HashMap)mapBtn.get(keyBtn);
                for(Object keyBtnDetail : mapBtnDetail.keySet()) {
                    //System.out.println(keyBtnDetail + ":" + mapBtnDetail.get(keyBtnDetail));
                    //System.out.println(mapBtnDetail.get(keyBtnDetail));
                    List<Map> list = (List)mapBtnDetail.get(keyBtnDetail);
                    //System.out.println(list.get(0) + "," + list.get(1));
                    Map list0 = list.get(0);
                    for(Object key0 : list0.keySet()){
                        //System.out.println(key0 + ":" + list0.get(key0));
                        String mResImg, cResImg;
                        if(key0.equals("mResImg")) {
                            //System.out.println("mResImg:" + list0.get(key0));
                            mResImg = list0.get(key0).toString();
                            System.out.println(mResImg);
                        }else if(key0.equals("cResImg")) {
                            //System.out.println("cResImg:" + list0.get(key0));
                            cResImg = list0.get(key0).toString();
                            System.out.println(cResImg);
                        }
                        //DynamicTest testBtn = dynamicTest(keyBtnDetail.toString(), () -> assertNotNull(""));
                        dynamicTests.add(dynamicTest(key + "..." + keyBtnDetail.toString(),
                                () -> assertNotNull("")));
                        //todo:如何把testBtn放到testPage下成为其子测试用例
                    }
                }
            }
        }

        return dynamicTests.stream();
    }

    public String addHtml(){
        Allure.addDescriptionHtml("");
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
        Method method = New_Old_CompareTest.class.getDeclaredMethod(methodName,classes);
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