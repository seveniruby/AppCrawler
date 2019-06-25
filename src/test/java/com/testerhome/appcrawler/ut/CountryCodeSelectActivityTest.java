package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.Report;
import com.testerhome.appcrawler.ReportFactory;
import com.testerhome.appcrawler.data.AbstractElementStore;
import com.testerhome.appcrawler.plugin.junit5.AllureTemplate;

public class CountryCodeSelectActivityTest extends AllureTemplate {
    public CountryCodeSelectActivityTest() {
        Report var1 = ReportFactory.getReportEngine("junit5");
        AbstractElementStore var2 = var1.loadResult("E://elements.yml");
        ReportFactory.initStore(var2);
        super.pageName = "com.xueqiu.android.CountryCodeSelectActivity";
        ReportFactory.showCancel_$eq(true);
    }
}
