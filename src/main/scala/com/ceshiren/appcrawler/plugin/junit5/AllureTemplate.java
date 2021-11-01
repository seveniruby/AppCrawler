package com.ceshiren.appcrawler.plugin.junit5;

import com.ceshiren.appcrawler.plugin.report.ReportFactory;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AllureTemplate {
    //pageNum : activity的名字
    public String pageName="";
    @TestFactory
    Collection<DynamicTest> AllElementInfo() {

        ArrayList<DynamicTest> arrayList = new ArrayList<>();
        System.out.println(pageName + "getSelected: " + ReportFactory.getSelected(pageName).size());
        ReportFactory.getSelected(pageName).forEach(value->{
            arrayList.add(dynamicTest(String.format("index=%d action=%s, xpath=%s",
                        value.getClickedIndex(),
                        value.getAction(),
                        value.getElement().getXpath()
                    ),
                    ()->{
                        System.out.println(String.format("req image: %s\nres image: %s\n", value.getReqImg(), value.getResImg()));
                        Allure.addAttachment("req image", value.getReqImg());
                        Allure.addAttachment("res image", value.getResImg());
                        Allure.addAttachment("res dom", value.getResDom());
                    }));
        });
        return arrayList;
    }
}