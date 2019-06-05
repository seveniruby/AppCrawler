package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.Report;
import com.testerhome.appcrawler.ReportFactory;
import com.testerhome.appcrawler.data.AbstractElementStore;
import com.testerhome.appcrawler.plugin.junit5.AllureTemplate;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AllureTemplateTestcase extends AllureTemplate {
    public AllureTemplateTestcase(){
        Report report=ReportFactory.genReport("junit5");
        AbstractElementStore store=report.loadResult("/private/tmp/xueqiu/20190530123423/elements.yml");
        ReportFactory.initStore(store);

        //todo: 老的数据不再兼容新的代码，需要重跑
        this.pageName="com.xueqiu.android.UserProfileActivity";
        ReportFactory.showCancel_$eq(true);
    }

    @Test
    public void run(){

    }
}