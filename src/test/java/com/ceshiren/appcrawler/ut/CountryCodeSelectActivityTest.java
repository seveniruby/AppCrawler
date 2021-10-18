package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.Report;
import com.ceshiren.appcrawler.ReportFactory;
import com.ceshiren.appcrawler.URIElementStore;
import com.ceshiren.appcrawler.plugin.junit5.AllureTemplate;

public class CountryCodeSelectActivityTest extends AllureTemplate {
    public CountryCodeSelectActivityTest() {
        Report var1 = ReportFactory.getReportEngine("junit5");
        URIElementStore var2 = var1.loadResult("E://elements.yml");
        ReportFactory.initStore(var2);
        super.pageName = "com.xueqiu.android.CountryCodeSelectActivity";
        ReportFactory.showCancel_$eq(true);
    }
}
