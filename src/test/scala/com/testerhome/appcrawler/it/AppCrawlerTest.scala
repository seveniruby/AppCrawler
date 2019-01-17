package com.testerhome.appcrawler.it

import com.testerhome.appcrawler.AppCrawler
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class AppCrawlerTest extends FunSuite with BeforeAndAfterEach {

  override def beforeEach() {

  }

  override def afterEach() {

  }

  test("testGetPageSource") {

  }

  test("xueqiu appium crawler"){
    AppCrawler.main(Array(
      "--capability",
        //"app=/Users/seveniruby/Downloads/com.xueqiu.android_11.10.2_190.apk," +
        "appPackage=com.xueqiu.android," +
        "appActivity=.view.WelcomeActivityAlias," +
        "noReset=false," +
        "automationName=uiautomator2," +
        "autoGrantPermissions=true," +
        "ignoreUnimportantViews=true," +
        "disableAndroidWatchers=true",
      "-o",
      s"/tmp/xueqiu/${new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)}",
      "-y",
      "{ blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ], " +
        "urlBlackList: [ .*StockDetail.* ] }",
      "-vv"
    )
    )
  }


  test("xueqiu appium crawler with new data"){
    AppCrawler.main(Array(
      "--capability",
      //"app=/Users/seveniruby/Downloads/com.xueqiu.android_11.10.2_190.apk," +
      "appPackage=com.xueqiu.android," +
        "appActivity=.view.WelcomeActivityAlias," +
        "noReset=false," +
        "automationName=uiautomator2," +
        "autoGrantPermissions=true," +
        "ignoreUnimportantViews=true," +
        "disableAndroidWatchers=true",
      "-o",
      s"/tmp/xueqiu/${new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)}",
      "-y",
      "{ blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ], useNewData: true, " +
        "urlBlackList: [ .*StockDetail.* ] }",
      "-vv"
    )
    )
  }

  test("zaker appium crawler"){
    AppCrawler.main(Array(
      "--capability",
      "appPackage=com.myzaker.ZAKER_Phone," +
        "appActivity=.view.LogoActivity," +
        "noReset=false," +
        "automationName=uiautomator2," +
        "autoGrantPermissions=true," +
        "ignoreUnimportantViews=true," +
        "disableAndroidWatchers=true",
      "-o",
      s"/tmp/xueqiu/${new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)}",
      "-y",
      "{ blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ], " +
        "urlBlackList: [ .*StockDetail.* ] }",
      "-vv"
    )
    )
  }


  test("report"){
    AppCrawler.main(Array(
      "-r",
      "/tmp/xueqiu/20181201003148",
      "-vv"
    )
    )

  }

  test("message appium crawler"){
    AppCrawler.main(Array(
      "--capability",
      "appPackage=com.android.messaging," +
        "appActivity=.ui.conversationlist.ConversationListActivity," +
        "noReset=false," +
        "automationName=uiautomator2",
      "-o",
      s"/Volumes/ram/message/${new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)}",
      "-y",
      "blackList: [ {xpath: action_night}, {xpath: action_setting}, {xpath: '.*[0-9\\.]{2}.*'} ]",
      "--verbose"
    )
    )
  }



}
