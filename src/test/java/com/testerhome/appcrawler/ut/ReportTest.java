package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.Crawler;
import com.testerhome.appcrawler.URIElement;
import com.testerhome.appcrawler.hbh.NewElementInfo;
import com.testerhome.appcrawler.hbh.NewURIElementStore;
import com.testerhome.appcrawler.plugin.ReportPlugin;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @Test
    public void test1(){
        ReportPlugin reportPlugin = new ReportPlugin();
        Crawler crawler = new Crawler();
        reportPlugin.setCrawer(crawler);
        URIElement element1 = new URIElement("a", "b", "c",
                "d", "e","","","","",
                "","",0,0,0,0,"");
        NewElementInfo info1 = new NewElementInfo();
        info1.setUriElement(element1);
        info1.setAction(NewURIElementStore.Status.SKIPPED);

        URIElement element2 = new URIElement("aa", "bb", "cc",
                "dd", "ee","","","","",
                "","",0,0,0,0,"");
        NewElementInfo info2 = new NewElementInfo();
        info2.setUriElement(element2);
        info2.setAction(NewURIElementStore.Status.SKIPPED);

        NewURIElementStore elementsStore=new NewURIElementStore();
        elementsStore.getNewElementStore().put(element1.toString(),info1);
        elementsStore.getNewElementStore().put(element2.toString(),info2);
        reportPlugin.saveTestCase(elementsStore, "/tmp/");
    }

    @Test
    public void test2(){

    }

}
