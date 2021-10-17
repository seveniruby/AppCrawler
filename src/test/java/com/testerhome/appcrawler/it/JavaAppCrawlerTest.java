package com.ceshiren.appcrawler.it;

import com.ceshiren.appcrawler.AppCrawler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class JavaAppCrawlerTest {

    // 测试-o参数
    @Test
    @Tag("it")
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
        //todo: bug 2.6.0 page source error超过次数后未重启session
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
             "/tmp/xueQiu400/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").
                format(new java.util.Date().getTime()),
//             "-c",
//             "src/test/java/com/testerhome/appcrawler/it/xueqiu_conf.yml",
                "-y",
                "{ useNewData: false, tagLimitMax: 3, " +
                        "blackList: [ {xpath: '更新'},{xpath: '检测'} ] " +
//                        "urlBlackList: [ .*StockDetail.* ]" +
                        "}",
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
                "src/test/java/com/testerhome/appcrawler/it/xueqiu_conf.yml",
                "-vv"
        });
    }

    // ApiDemo
    @Test()
    public void testApiDemo(){
        AppCrawler.main(new String[]{
                "--capability",
                "appPackage=com.example.hbh.myshop," +
                        "appActivity=.app.SplashActivity," +
                        "noReset=false," +
                        "automationName=uiautomator2," +
                        "autoGrantPermissions=true," +
                        "ignoreUnimportantViews=true," +
                        "disableAndroidWatchers=true",
                "-o",
                "/tmp/myshop/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").
                        format(new java.util.Date().getTime()),
                "-vv"
        });
    }
}
