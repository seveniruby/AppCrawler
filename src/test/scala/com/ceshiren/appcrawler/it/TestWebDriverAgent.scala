package com.ceshiren.appcrawler.it

import com.ceshiren.appcrawler.core.AppiumSuite

import java.net.URL
import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.FunSuite

import scala.jdk.CollectionConverters._

/**
  * Created by seveniruby on 16/6/3.
  */
class TestWebDriverAgent extends AppiumSuite{
  test("use remote webdriver"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app", "/Users/seveniruby/projects/snowball-ios/DerivedData/Snowball/Build/Products/Debug-iphonesimulator/Snowball.app")
    capability.setCapability("bundleId", "com.xueqiu")
    capability.setCapability("fullReset", "true")
    capability.setCapability("noReset", "true")
    capability.setCapability("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    capability.setCapability("automationName", "XCUITest")
    capability.setCapability("platformName", "ios")
    capability.setCapability("deviceName", "iPhone Simulator")
    capability.setCapability("bundleId", "com.xueqiu")

    //val url="http://192.168.100.65:7771"
    val url="http://127.0.0.1:4723/wd/hub"
    val driver=new RemoteWebDriver(new URL(url), capability)
    println(driver.getPageSource)
  }


  test("use remote webdriver meituan"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app", "/Users/seveniruby/Downloads/app/waimai.app")
    capability.setCapability("bundleId", "com.meituan.iToGo.ep")
    //capability.setCapability("fullReset", false)
    //capability.setCapability("noReset", true)
    //capability.setCapability("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    capability.setCapability("automationName", "XCUITest")
    capability.setCapability("platformName", "ios")
    capability.setCapability("deviceName", "iPhone 6")
    capability.setCapability("platformVersion", "10.2")
    capability.setCapability("autoAcceptAlerts", true)
    //capability.setCapability("webDriverAgentUrl", "http://172.18.118.90:8100/")

    //val url="http://192.168.100.65:7771"
    //val url="http://127.0.0.1:8100"
    val url="http://127.0.0.1:4730/wd/hub"
    val driver=new RemoteWebDriver(new URL(url), capability)

    while(true){
      Thread.sleep(2000)
      println(driver.getPageSource)
    }

  }

  test("use remote webdriver xueqiu"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app", "/Users/seveniruby/projects/snowball-ios/DerivedData/Snowball/Build/Products/Debug-iphonesimulator/Snowball.app")
    capability.setCapability("bundleId", "com.xueqiu")
    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    //capability.setCapability("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    capability.setCapability("automationName", "XCUITest")
    capability.setCapability("platformName", "ios")
    capability.setCapability("deviceName", "iPhone Simulator")
    capability.setCapability("bundleId", "com.xueqiu")
    capability.setCapability("autoAcceptAlerts", true)


    //val url="http://192.168.100.65:7771"
    //val url="http://127.0.0.1:8100"
    val url="http://127.0.0.1:4730/wd/hub"
    val driver=new RemoteWebDriver(new URL(url), capability)

    while(true){
      Thread.sleep(2000)
      driver.findElementsByXPath("//*").asScala.foreach(e=>{
        println(s"tag=${e.getTagName} text=${e.getText}")
      })
      println(driver.getPageSource)
      println("==============")
    }

  }
}
