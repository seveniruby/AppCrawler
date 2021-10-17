package com.ceshiren.appcrawler.it

import java.net.URL
import java.util.concurrent.TimeUnit

import com.ceshiren.appcrawler.driver.AppiumClient
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.{AndroidMobileCapabilityType, MobileCapabilityType}
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.FunSuite

import scala.io.Source

/**
  * Created by seveniruby on 16/9/24.
  */
class TestAppium extends FunSuite{
  val a=new AppiumClient()

  test("single session"){
    val capa=new DesiredCapabilities()
    capa.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.xueqiu.android")
    capa.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".view.WelcomeActivityAlias")
    capa.setCapability(MobileCapabilityType.DEVICE_NAME, "demo")
    val driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capa)
  }

  test("test get window size"){
    //System.setProperty("webdriver.http.factory", "apache")
    val capa=new DesiredCapabilities()
    capa.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.xueqiu.android")
    capa.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".view.WelcomeActivityAlias")
    capa.setCapability(MobileCapabilityType.DEVICE_NAME, "demo")
    val driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:5723/wd/hub/"), capa)
    Thread.sleep(10000)
    println(driver.manage().window().getSize)
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    println(driver.getPageSource)
  }

  test("test xueqiu agreen"){
    val capa=new DesiredCapabilities()
    capa.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.xueqiu.android")
    capa.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".view.WelcomeActivityAlias")
    capa.setCapability(MobileCapabilityType.DEVICE_NAME, "demo")
    capa.setCapability("uiautomationName", "uiautomator2")
    val driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capa)
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    driver.findElementById("agree").click()
    println(driver.getPageSource)
    println(driver.getPageSource)
  }


}
