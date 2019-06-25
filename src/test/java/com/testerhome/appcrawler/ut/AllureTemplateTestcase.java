package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.Report;
import com.testerhome.appcrawler.ReportFactory;
import com.testerhome.appcrawler.data.AbstractElementStore;
import com.testerhome.appcrawler.plugin.junit5.AllureTemplate;

public class AllureTemplateTestcase extends AllureTemplate {
    public AllureTemplateTestcase(){
        Report report=ReportFactory.getReportEngine("junit5");
        AbstractElementStore store=report.loadResult("E://elements.yml");
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