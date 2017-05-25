package com.testerhome.appcrawler.it

import scala.collection.JavaConversions._

import java.net.URL

import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.FunSuite

/**
  * Created by seveniruby on 2017/5/12.
  */
class TestIOS extends FunSuite{
  test("ios测试"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app",
      "/Users/seveniruby/projects/ios-uicatalog/build/Debug-iphonesimulator/UICatalog.app")
    capability.setCapability("bundleId", "com.example.apple-samplecode.UICatalog")
    //capability.setCapability("appPackage", "com.example.apple-samplecode.UICatalog")
    //capability.setCapability("appActivity", "com.example.apple-samplecode.UICatalog")


    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
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
    driver.findElementsByXPath("//*").foreach(x=>{
      println(x.getText)
    })

  }

  test("android"){
    val capability=new DesiredCapabilities()
    capability.setCapability("app", "")
    capability.setCapability("appPackage", "com.gotokeep.keep")
    capability.setCapability("appActivity", ".activity.SplashActivity")
    //capability.setCapability("appWaitActivity", "MainActivity")

    capability.setCapability("fullReset", "false")
    capability.setCapability("noReset", "true")
    //capability.setCapability("udid", "4F05E384-FE32-43DE-8539-4DC3E2EBC117")
    //capability.setCapability("automationName", "uiautomator")
    capability.setCapability("automationName", "appium")
    capability.setCapability("platformName", "android")
    capability.setCapability("platformVersion", "")
    capability.setCapability("deviceName", "dddd")
    //capability.setCapability("autoAcceptAlerts", true)


    //val url="http://192.168.100.65:7771"
    //val url="http://127.0.0.1:8100"
    val url="http://127.0.0.1:4723/wd/hub"
    val driver=new RemoteWebDriver(new URL(url), capability)
    Thread.sleep(3000)

    driver.findElementsByXPath("//*").foreach(x=>{
      println(x.getText)
    })

    driver.findElementByXPath("//*[@text='跑步']").click()


  }




}
