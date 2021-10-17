package com.ceshiren.appcrawler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerTest {

    static String caps= "appPackage=com.example.android.apis," +
            "appActivity=.ApiDemos," +
            "noReset=false," +
            "automationName=uiautomator2," +
            "autoGrantPermissions=true," +
            "ignoreUnimportantViews=true," +
            "disableAndroidWatchers=true";
    @BeforeAll
    static void beforeAll(){

    }

    @Test
    @Tag("it")
    @Tag("feature")

    void runSteps() {
        AppCrawler.main(new String[]{
                "--capability", caps,
                "-y", "{ maxDepth: 3, testcase: { name: runSteps test, steps: [ " +
                "{xpath: \"//*[@text='Views']\", then: [ \"//*[@text='Buttons']\" ]}, " +
                "{xpath: \"//*[@text='Buttons']\", then: [ \"//*[@text='SMALL']\", \"//*[@text='OFF']\" ]} " +
                "]}}",
                "-vv"
        });
    }

    @Test
    @Tag("it")
    void runSteps2() {
        AppCrawler.main(new String[]{
                "--capability", caps,
                "-y", "{ selectedList: [], testcase: { name: runSteps test, steps: [ " +
                "{xpath: \"//*[@text='Views']\", then: [ \"//*[@text='Buttons']\" ]}, " +
                "{xpath: \"//*[@text='Buttons']\", then: [ \"//*[@text='SMALL']\", \"//*[@text='OFF']\" ]} " +
                "]}}",
                "-vv"
        });
    }
}