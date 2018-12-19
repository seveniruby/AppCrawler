package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.TData;
import com.testerhome.appcrawler.URIElement;
import com.testerhome.appcrawler.hbh.NewURIElementStore;
import org.junit.jupiter.api.Test;

public class StoreTest {

    @Test
    public void test2(){
        NewURIElementStore store = new NewURIElementStore();

        URIElement element1 = new URIElement("a", "b", "c",
                "d", "e","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element1);
        store.setElementClicked(element1);
        store.saveReqDom("<1></1>");
        store.saveResDom("<1_res></1_res>");

        URIElement element2 = new URIElement("aa", "bb", "cc",
                "dd", "ee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element2);
        store.setElementSkip(element2);

        URIElement element3 = new URIElement("aaa", "bbb", "ccc",
                "ddd", "eee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element3);
        store.setElementClicked(element3);
        store.saveReqDom("<2></2>");
        store.saveResDom("<2_res></2_res>");

        URIElement element4 = new URIElement("aaaa", "bbbb", "cccc",
                "dddd", "eeee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element4);
        store.setElementClicked(element4);
        store.saveReqDom("<3></3>");
        store.saveResDom("<3_res></3_res>");


        URIElement element5 = new URIElement("aaaaa", "bbbbb", "ccccc",
                "ddddd", "eeeee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element5);
        store.setElementClicked(element5);
        store.saveReqDom("<4></4>");
        store.saveResDom("<4_res></4_res>");

        AppCrawler.log().info(store);
        String str = TData.toYaml(store);
        AppCrawler.log().info(str);

    }
}
