package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.URIElement;
import org.junit.jupiter.api.Test;

public class ElementUrlTest {

    @Test
    public void test1(){
        URIElement element = new URIElement("com.xueqiu.android.MainActivity",
                "TextView", "TextView",
                "agree",
                "","好的","","7","","","","",
                0,0,0,0,"");

        System.out.println(element.elementUri());
    }
}