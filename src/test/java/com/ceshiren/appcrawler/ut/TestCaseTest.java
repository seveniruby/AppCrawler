package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.AppCrawler;
import org.junit.Test;

import java.util.Date;

public class TestCaseTest {

    // uiautomator2
    @Test
    public void testCase2(){
        AppCrawler.main(new String[]{
                "--capability",
                    "appPackage=com.xueqiu.android,"+
                    "appActivity=.view.WelcomeActivityAlias,"+
                    "noReset=true,"+
                    "ignoreUnimportantViews=false,"+
                    "waitForIdleTimeout=100,"+
                    "automationName=uiautomator2",
                "-o",
                    "/temp/xueqiu/ut/testCase/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new Date().getTime()),
                "-y",
                    "{ " +
                         "testcase: {" +
                            "name: testLogin," +
                            "steps: [" +
                                "{"+
                                    "given: [\"//*[contains(@resource-id,'tab_name') and @text='自选']\"],"+
                                    "when: " +
                                    "{" +
                                        "xpath: \"//*[contains(@resource-id,'tab_name') and @text='自选']\","+
                                        "action: click"+
                                    "},"+
                                    "then: [\"//*[contains(@resource-id,'portfolio_stockName')]\"]"+
                                "},"+
                                "{"+
                                    "given: [\"//*[contains(@resource-id,'portfolio_stockName')]\"],"+
                                    "when: " +
                                    "{" +
                                        "xpath: \"//*[contains(@resource-id,'portfolio_stockName')]\","+
                                        "action: click"+
                                    "}"+
                                "}"+
                            "]"+
                         "}" +
                    "}"
        });
    }

    // uiautomator1
    @Test
    public void testCase1(){
        AppCrawler.main(new String[]{
                "--capability",
                "appPackage=com.xueqiu.android,"+
                        "appActivity=.view.WelcomeActivityAlias,"+
                            "noReset=true",
                "-o",
                "/temp/xueqiu/ut/testCase/" + new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new Date().getTime()),
                "-y",
                "{ " +
                        "testcase: {" +
                        "name: testLogin," +
                        "steps: [" +
                        "{"+
                        "given: [\"//*[contains(@resource-id,'user_profile_icon')]\"],"+
                        "when: " +
                        "{" +
                        "xpath: \"//*[contains(@resource-id,'user_profile_icon')]\","+
                        "action: click"+
                        "},"+
                        "then: [\"//*[contains(@resource-id,'tv_login')]\"]"+
                        "},"+
                        "{"+
                        "given: [\"//*[contains(@resource-id,'tv_login')]\"],"+
                        "when: " +
                        "{" +
                        "xpath: \"//*[contains(@resource-id,'tv_login')]\","+
                        "action: click"+
                        "},"+
                        "then: [\"//*[contains(@resource-id,'tv_login_by_phone_or_others')]\"]"+
                        "},"+
                        "{"+
                        "given: [\"//*[contains(@resource-id,'tv_login_by_phone_or_others')]\"],"+
                        "when: " +
                        "{" +
                        "xpath: \"//*[contains(@resource-id,'tv_login_by_phone_or_others')]\","+
                        "action: click"+
                        "},"+
                        "then: [\"//*[contains(@resource-id,'register_phone_number')]\"]"+
                        "},"+
                        "{"+
                        "given: [\"//*[contains(@resource-id,'register_phone_number')]\"],"+
                        "when: " +
                        "{" +
                        "xpath: \"//*[contains(@resource-id,'register_phone_number')]\","+
                        "action: \"12345678901\""+
                        "},"+
                        "then: [\"//*[contains(@resource-id,'button_next')]\"]"+
                        "}"+
                        "]"+
                        "}" +
                        "}"
        });
    }
}
