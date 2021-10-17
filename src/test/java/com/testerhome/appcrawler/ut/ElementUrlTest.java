package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.data.PathElement;
import org.junit.jupiter.api.Test;

public class ElementUrlTest {

    @Test
    public void test1(){
        PathElement element = new PathElement("com.xueqiu.android.MainActivity",
                "TextView",
                "agree",
                "","好的","","7","","","","",
                0,0,0,0,"");

        System.out.println(element.elementUri());
    }
}