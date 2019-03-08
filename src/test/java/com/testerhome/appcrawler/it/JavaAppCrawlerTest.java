package com.testerhome.appcrawler.it;

import com.testerhome.appcrawler.AppCrawler;
import org.junit.jupiter.api.Test;

public class JavaAppCrawlerTest {

    // 测试-o参数
    @Test
    public void testO(){
        AppCrawler.main(new String[]{
                "--capability",
                "appPackage=com.example.hbh.myapplication," +
                        "appActivity=.appWidget.testActivity," +
                        "noReset=false," +
                        "automationName=uiautomator2," +
                        "autoGrantPermissions=true," +
                        "ignoreUnimportantViews=true," +
                        "disableAndroidWatchers=true",
//                "-o",
//                "/tmp/myApp/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").
//                        format(new java.util.Date().getTime()),
                "-y",
                "{ useNewData: true}",
                "-vv"
        });
    }

    //自定义app测试全屏图片bug
    @Test
    public void testMy(){
        AppCrawler.main(new String[]{
                "--capability",
                "appPackage=com.example.hbh.myapplication," +
                        "appActivity=.appWidget.testActivity," +
                        "noReset=false," +
                        "automationName=uiautomator2," +
                        "autoGrantPermissions=true," +
                        "ignoreUnimportantViews=true," +
                        "disableAndroidWatchers=true",
                "-o",
                "/tmp/myApp/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").
                        format(new java.util.Date().getTime()),
                "-y",
                "{ useNewData: true}",
                "-vv"
        });
    }

    // 雪球App
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
             "{ blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ], useNewData: true, " +
             "urlBlackList: [ .*StockDetail.* ]}",
             "-vv"
        });
    }

    // 雪球App的图片测试
    @Test
    public void testXqImage(){
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
                "-c",
                "src/test/java/com/testerhome/appcrawler/it/xueqiu_bigimg.yml",
                "-vv"
        });
    }

    // ApiDemo
    @Test()
    public void testApiDemo(){
        AppCrawler.main(new String[]{
                "--capability",
                "appPackage=io.appium.android.apis," +
                        "appActivity=.ApiDemos," +
                        "noReset=false," +
                        "automationName=uiautomator2," +
                        "autoGrantPermissions=true," +
                        "ignoreUnimportantViews=true," +
                        "disableAndroidWatchers=true",
                "-o",
                "/tmp/api1/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").
                        format(new java.util.Date().getTime()),
                "-y",
                "{ blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ], " +
                        "urlBlackList: [ .*StockDetail.* ], useNewData: true }",
                "-vv"
        });
    }
}
