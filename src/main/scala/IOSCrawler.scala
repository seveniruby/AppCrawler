import java.net.URL

import io.appium.java_client.AppiumDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities

import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by seveniruby on 15/12/10.
  */
class IOSCrawler extends Crawler {
  if(conf.selectedList.length==0) {
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

  override def setupApp(app: String, url: String = "http://127.0.0.1:4723/wd/hub"):Unit={
    platformName = "iOS"
    super.setupApp()
    capabilities.setCapability("platformName", "iOS")
    conf.iosCapability.foreach(kv=>capabilities.setCapability(kv._1, kv._2))
    //capabilities.setCapability(MobileCapabilityType.APP, "http://xqfile.imedao.com/android-release/xueqiu_681_10151900.apk")
    //driver = new XueqiuDriver[WebElement](new URL("http://127.0.0.1:4729/wd/hub"), capabilities)
    val url=conf.iosCapability("appium")
    driver = new IOSDriver[WebElement](new URL(url), capabilities)
    driver.launchApp()
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
    if (nav.length > 0) {
      println(s"url=${nav(0)("name")}")
      return nav(0)("name")
    } else {
      val screenName = getSchema().takeRight(5)
      println(s"url=${screenName}")
      return screenName
    }
  }


  /**
    * 尝试识别当前的页面
    * @return
    */
  override def getSchema(): String = {
    val nodeList = getAllElements("//UIAWindow[1]//*[not(ancestor-or-self::UIATableView)]")
    //todo: 未来应该支持黑名单
    val schemaBlackList = List("UIATableCell", "UIATableView", "UIAScrollView")
    md5(nodeList.filter(node => schemaBlackList.contains(node("tag")) == false).map(node => node("tag")).mkString(""))
  }

  override def getRuleMatchNodes(): ListBuffer[Map[String, String]] = {
    (getAllElements("//UIAWindow[1]//*") ++
      getAllElements("//UIAWindow[3]//*") ++
      getAllElements("//UIAWindow//UIAButton")).distinct
  }
}
