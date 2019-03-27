package com.testerhome.appcrawler.report;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
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

@Epic("新老版本对比")
public class AllureDiffTest {
    String mResImg = null, mReqImg, cResImg, cReqImg;
    @Feature("当前页面")
    @TestFactory
    Stream<DynamicTest> dynamicTestsExample() throws Exception {
        List<DynamicTest> dynamicTests = new ArrayList<>();
        ReadYaml read = new ReadYaml();
        Map mapRoot = read.convert2Map("/tmp/xueQiu400/reportDiff/diff.yml");
        //System.out.println(mapRoot);
        for(Object key : mapRoot.keySet()) {
            //System.out.println(key + ":" + mapRoot.get(key));
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
                    Map list1 = list.get(1);

                    for(Object key0 : list0.keySet()){
                        //c:new   m:old

                        String img = list0.get(key0).toString().equals("") ? "src/main/resources/404.png" : list0.get(key0).toString();
                        switch (key0.toString()){
                            case "cReqImg":
                                cReqImg = img;
                                break;
                            case "mResImg":
                                mResImg = img;
                                break;
                            case "mReqImg":
                                mReqImg = img;
                                break;
                            case "cResImg":
                                cResImg = img;
                                break;
                        }

                    }

                    dynamicTests.add(dynamicTest(key + "..." + keyBtnDetail.toString(),
                            () -> assertNotNull(addHtml("id", "tag")
                                    + lifeCycleAttach(mResImg,"image/png", ".png", new FileInputStream(mResImg))
                                    + lifeCycleAttach(cResImg,"image/png", ".png", new FileInputStream(cResImg)))));
                }
            }
        }
        return dynamicTests.stream();
    }

    public String addHtml(String id, String tag){
        Allure.addDescriptionHtml("<table><tr><td>id</td><td>"+id+"</td></tr><tr><td>tag</td><td>"+tag+"</td></tr></table>");
        return null;
    }

    public String lifeCycleAttach(String name, String type, String extraName, FileInputStream path){
        Allure.getLifecycle().addAttachment(name, type, extraName, path);
        return null;
    }



}
