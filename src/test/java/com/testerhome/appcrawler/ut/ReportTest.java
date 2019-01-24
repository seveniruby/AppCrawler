package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.Crawler;
import com.testerhome.appcrawler.URIElement;
import com.testerhome.appcrawler.data.PathElementInfo;
import com.testerhome.appcrawler.data.PathElementStore;
import com.testerhome.appcrawler.diff.CrawlerDiff;
import com.testerhome.appcrawler.plugin.ReportPlugin;
import com.testerhome.appcrawler.report.MvnReplace;
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
        PathElementInfo info1 = new PathElementInfo();
        info1.setElement(element1);
        info1.setAction(PathElementStore.Status.SKIPPED);

        URIElement element2 = new URIElement("aa", "bb", "cc",
                "dd", "ee","","","","",
                "","",0,0,0,0,"");
        PathElementInfo info2 = new PathElementInfo();
        info2.setElement(element2);
        info2.setAction(PathElementStore.Status.SKIPPED);

        PathElementStore elementsStore=new PathElementStore();
        elementsStore.getStore().put(element1.toString(),info1);
        elementsStore.getStore().put(element2.toString(),info2);
        reportPlugin.saveTestCase(elementsStore, "/tmp/");
    }

    @Test
    public void test2() throws Exception {
//        MvnReplace.runTest();
        CrawlerDiff.diffSuite("","","");
    }

}
