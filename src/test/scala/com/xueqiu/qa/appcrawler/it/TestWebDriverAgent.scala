package com.xueqiu.qa.appcrawler.it

import java.net.URL

import com.xueqiu.qa.appcrawler.MiniAppiumSuite
import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 16/6/3.
  */
class TestWebDriverAgent extends MiniAppiumSuite{
  test("test facebook webdriver"){
    config("app", "/Users/seveniruby/projects/snowball-ios/DerivedData/Snowball/Build/Products/Debug-iphonesimulator/Snowball.app")
    config("bundleId", "com.xueqiu")
    config("fullReset", "true")
    config("noReset", "true")
    config("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    config("automationName", "XCUITest")
    //appium("http://192.168.100.65:8100")
    appium("http://127.0.0.1:4723/wd/hub/")

    println(driver.getPageSource)
    see("选股策略").tap()
    quit()
  }

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

}
