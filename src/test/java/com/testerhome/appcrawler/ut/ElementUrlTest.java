package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.hbh.NewURIElement;
import org.junit.jupiter.api.Test;

public class ElementUrlTest {

    @Test
    public void test1(){
        NewURIElement element = new NewURIElement("com.xueqiu.android.MainActivity",
                "TextView",
                "agree",
                "","好的","","7","","","","",
                0,0,0,0,"");

        System.out.println(element.getElementUrl());
    }
}