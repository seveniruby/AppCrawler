package com.ceshiren.appcrawler.it

import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.FunSuite

import java.net.URL
import scala.jdk.CollectionConverters._

class TestSauceLabs extends FunSuite{

  val app = "/Users/seveniruby/Desktop/baiduyun/百度云同步盘/seven/Dropbox/sihanjishu/startup/测吧/业务/如期/如期-iOS安装包/PLM.ipa"
  //val app="/Users/seveniruby/Desktop/baiduyun/百度云同步盘/seven/Dropbox/sihanjishu/startup/测吧/业务/如期/PLM.zip"

  test("ios测试"){
    val capability=new DesiredCapabilities()
    //capability.setCapability("app",  app)
    //capability.setCapability("bundleId", "com.example.apple-samplecode.UICatalog")
    //capability.setCapability("appPackage", "com.example.apple-samplecode.UICatalog")
    //capability.setCapability("appActivity", "com.example.apple-samplecode.UICatalog")


    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "false")
    //capability.setCapability("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    capability.setCapability("automationName", "XCUITest")
    capability.setCapability("platformName", "ios")
    capability.setCapability("platformVersion", "10.2")
    capability.setCapability("deviceName", "iPhone 7")
    capability.setCapability("autoAcceptAlerts", true)


    //val url="http://192.168.100.65:7771"
    //val url="http://127.0.0.1:8100"
    val url="http://127.0.0.1:4723/wd/hub"
    val driver=new RemoteWebDriver(new URL(url), capability)
    println(driver.getPageSource)
    driver.findElementsByXPath("//*[@label='OK']") match {
      case array if array.size()>0 => array.asScala.head.click()
      case _ => println("no OK alert")
    }
    driver.findElementsByXPath("//*").asScala.foreach(x=>{
      println(x)
      println(x.getText)
    })

  }

  test("ios测试 saucelabs"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app",  app)

    capability.setCapability("bundleId", "www.ruqi.com")
    //capability.setCapability("bundleId", "com.example.apple-samplecode.UICatalog")
    //capability.setCapability("appPackage", "com.example.apple-samplecode.UICatalog")
    //capability.setCapability("appActivity", "com.example.apple-samplecode.UICatalog")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "false")
    //capability.setCapability("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    capability.setCapability("automationName", "XCUITest")
    capability.setCapability("platformName", "ios")
    capability.setCapability("platformVersion", "10.2")
    capability.setCapability("deviceName", "iPhone 7")
    capability.setCapability("autoAcceptAlerts", true)

    capability.setCapability("testobject_api_key", "E571F6B0932E4DB1BD8E554A97904A0C")
    capability.setCapability("testobject_app_id", "ruqi")
    capability.setCapability("testobject_suite_name ", "My Suite 1!")
    capability.setCapability("testobject_test_name", "My Test 1!")
    //capability.setCapability("name", "My Test 1!")


    // Set Appium version
    capability.setCapability("appiumVersion", "1.7.1")
    val url = "https://us1.appium.testobject.com/wd/hub"


    //val url="http://192.168.100.65:7771"
    //val url="http://127.0.0.1:8100"
    //val url="http://127.0.0.1:4723/wd/hub"
    val driver=new RemoteWebDriver(new URL(url), capability)
    println(driver.getPageSource)
  }

}
