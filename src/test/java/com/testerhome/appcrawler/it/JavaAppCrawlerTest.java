package com.testerhome.appcrawler.it;

import com.testerhome.appcrawler.AppCrawler;
import org.junit.jupiter.api.Test;

public class JavaAppCrawlerTest {

    @Test
    public void test1(){
        AppCrawler.main(new String[]{
             "--capability",
             "appPackage=com.xueqiu.android," +
             "appActivity=.view.WelcomeActivityAlias," +
             "noReset=false," +
             "automationName=uiautomator2," +
             "autoGrantPermissions=true," +
             "ignoreUnimportantViews=true," +
             "disableAndroidWatchers=true",
             "-o",
             "/tmp/xueqiu11/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").
                format(new java.util.Date().getTime()),
             "-y",
             "{ blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ], " +
             "urlBlackList: [ .*StockDetail.* ] }",
             "-vv"
        });
    }
}
