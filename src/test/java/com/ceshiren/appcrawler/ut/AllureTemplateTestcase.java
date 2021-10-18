package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.Report;
import com.ceshiren.appcrawler.ReportFactory;
import com.ceshiren.appcrawler.URIElementStore;
import com.ceshiren.appcrawler.plugin.junit5.AllureTemplate;

public class AllureTemplateTestcase extends AllureTemplate {
    public AllureTemplateTestcase(){
        Report report=ReportFactory.getReportEngine("junit5");
        URIElementStore store=report.loadResult("E://elements.yml");
        ReportFactory.initStore(store);

        //todo: 老的数据不再兼容新的代码，需要重跑
        //不明白以上todo注释的理解
        this.pageName="com.xueqiu.android.LoginActivity";
        ReportFactory.showCancel_$eq(true);
    }

    /*@Test
    public void run(){

    }*/
}