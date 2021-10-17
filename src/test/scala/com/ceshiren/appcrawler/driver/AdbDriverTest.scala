package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.AppCrawler
import org.scalatest.FunSuite

class AdbDriverTest extends FunSuite {

  test("testGetPageSource") {
    println(AdbDriverTest.adbDriver.getPageSource())
  }

  test("getAdb"){
    println(AdbDriverTest.adbDriver.getAdb())
  }

  test("screenshot"){
    println(AdbDriverTest.adbDriver.screenshot().getAbsolutePath)
  }

  test("getUrl"){
    println(AdbDriverTest.adbDriver.getAppName())
    println(AdbDriverTest.adbDriver.getUrl())

  }

  test("it"){
    AppCrawler.main(Array(
      "-y",
      "blackList: [ {xpath: action_night}, {xpath: action_setting} ]",
      "--capability",
      "appPackage=com.xueqiu.android," +
        "appActivity=.view.WelcomeActivityAlias," +
        "automationName=adb,noReset=false," +
        "udid=adb.wetest.qq.com:41272," +
        "autoGrantPermissions=true," +
        "ignoreUnimportantViews=true," +
        "disableAndroidWatchers=true",
      "-o",
      s"/tmp/xueqiu/adb/${new java.text.SimpleDateFormat("YYYYMMddHHmmss").format(new java.util.Date().getTime)}",
        "-vv"
    )
    )
  }

}

object AdbDriverTest{
  val adbDriver=new AdbDriver()
}
