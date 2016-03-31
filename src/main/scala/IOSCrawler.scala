import java.net.URL

import io.appium.java_client.AppiumDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by seveniruby on 15/12/10.
  */
class IOSCrawler extends Crawler {
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
    platformName = "iOS"
    super.setupAppium()
    capabilities.setCapability("platformName", "iOS")
    conf.iosCapability.foreach{ case (k,v)=>{
      capabilities.setCapability(k, v)
    }}
    //capabilities.setCapability(MobileCapabilityType.APP, "http://xqfile.imedao.com/android-release/xueqiu_681_10151900.apk")
    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    val url=conf.iosCapability("appium").toString
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
    if(superUrl!=""){
      println(s"url=${superUrl}")
      return superUrl
    }
    val nav = getAllElements("//UIANavigationBar[1]")
    if (nav.nonEmpty) {
      println(s"url=${nav.head("name")}")
      nav.head("name").toString
    } else {
      val screenName = getSchema().takeRight(5)
      println(s"url=${screenName}")
      screenName
    }
  }

  override def getRuleMatchNodes(): List[scala.collection.immutable.Map[String, Any]] = {
    getAllElements("//*[@visible='true' and @enabled='true' and @valid='true']")
  }
}
