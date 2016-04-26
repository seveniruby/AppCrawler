package com.xueqiu.qa.appcrawler

import java.net.URL

import io.appium.java_client.ios.IOSDriver
import org.openqa.selenium.WebElement

/**
  * Created by seveniruby on 15/12/10.
  */
class IOSCrawler extends Crawler {
  platformName = "iOS"
  if(conf.selectedList.isEmpty) {
    conf.selectedList.insertAll(0, Seq(
      "//UIAWindow[1]//UIATextField[@visible='true' and @enabled='true' and @valid='true']",
      "//UIAWindow[1]//UIASecureTextField[@visible='true' and @enabled='true' and @valid='true']",
      "//UIAWindow[1]//UIATableCell[@visible='true' and @enabled='true' and @valid='true']",
      "//UIAWindow[1]//UIAButton[@visible='true' and @enabled='true' and @valid='true']",
      "//UIAWindow[1]//UIAImage[@visible='true' and @enabled='true' and @valid='true']",
      "//UIAWindow[1]//UIACollectionCell[@visible='true' and @enabled='true' and @valid='true']"
    ))

    conf.firstList.insertAll(0, Seq(
      "//UIAWindow[3]//UIAButton[@visible='true' and @enabled='true' and @valid='true']",
      "//UIAWindow[2]//UIAButton[@visible='true' and @enabled='true' and @valid='true']"
    ))
  }

  override def setupAppium():Unit={
    super.setupAppium()
    capabilities.setCapability("platformName", "iOS")
    conf.capability.foreach{ case (k,v)=>{
      capabilities.setCapability(k, v)
    }}
    //capabilities.setCapability(MobileCapabilityType.APP, "http://xqfile.imedao.com/android-release/xueqiu_681_10151900.apk")
    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    val url=conf.capability("appium").toString
    driver = new IOSDriver[WebElement](new URL(url), capabilities)
    //driver.launchApp()
    getDeviceInfo()
  }

  /**
    * 用schema作为url替代
    * @return
    */
  override def getUrl(): String = {
    val superUrl=super.getUrl()
    lastAppName=appName
    appName=getAllElements("//UIAApplication").head.getOrElse("name", "").toString
    log.trace(s"appName = ${appName}")
    log.trace(getAllElements("//UIAApplication").head)
    val title=getAllElements("//UIANavigationBar").map(_.getOrElse("name", "").toString).mkString("")
    List(appName, title, superUrl).distinct.filter(_.nonEmpty).mkString("-")
  }
}
