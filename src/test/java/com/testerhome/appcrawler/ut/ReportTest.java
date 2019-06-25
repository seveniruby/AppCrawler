package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.Crawler;
import com.testerhome.appcrawler.ReportFactory;
import com.testerhome.appcrawler.URIElement;
import com.testerhome.appcrawler.data.PathElementInfo;
import com.testerhome.appcrawler.data.PathElementStore;
import com.testerhome.appcrawler.plugin.ReportPlugin;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @Test
    public void test1(){
        ReportPlugin reportPlugin = new ReportPlugin();
        Crawler crawler = new Crawler();
        reportPlugin.setCrawer(crawler);
        URIElement element1 = new URIElement("url_1", "tag_1", "id_1",
                "name_1", "text_1","","","","",
                "","",0,0,0,0,"");
        PathElementInfo info1 = new PathElementInfo();
        info1.setElement(element1);
        info1.setAction(PathElementStore.Status.SKIPPED);

        URIElement element2 = new URIElement("url_2", "tag_2", "id_2",
                "name_2", "text_2","","","","",
                "","",0,0,0,0,"");
        PathElementInfo info2 = new PathElementInfo();
        info2.setElement(element2);
        info2.setAction(PathElementStore.Status.SKIPPED);

        URIElement element3 = new URIElement("url_3", "tag_3", "id_3",
                "name_3", "text_2","","","","",
                "","",0,0,0,0,"");
        PathElementInfo info3 = new PathElementInfo();
        info3.setElement(element3);
        info3.setAction(PathElementStore.Status.CLICKED);

        URIElement element4 = new URIElement("url_4", "tag_4", "id_4",
                "name_4", "text_4","","","","",
                "","",0,0,0,0,"");
        PathElementInfo info4 = new PathElementInfo();
        info4.setElement(element4);
        info4.setAction(PathElementStore.Status.READY);



        PathElementStore elementsStore=new PathElementStore();
        elementsStore.getElementStoreMap().put(element1.toString(),info1);
        elementsStore.getElementStoreMap().put(element2.toString(),info2);
        elementsStore.getElementStoreMap().put(element3.toString(),info3);
        elementsStore.getElementStoreMap().put(element4.toString(),info4);

        ReportFactory.showCancel_$eq(true);
        ReportFactory.getReportEngine("scalatest");
        ReportFactory.initStore(elementsStore);
        ReportFactory.getInstance().genTestCase("/tmp/");
        ReportFactory.getInstance().runTestCase("");


    }

    @Test
    public void test2() throws Exception {
//        MvnReplace.runTest();
//        CrawlerDiff.diffSuite("","","");
    }


    @Test
    public void testAllureTemplate(){

    }

}
