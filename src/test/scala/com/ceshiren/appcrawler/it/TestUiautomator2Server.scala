package com.ceshiren.appcrawler.it

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest.FunSuite

import java.net.URL


class TestUiautomator2Server extends FunSuite{


  test("selenium remote"){
    val caps=new DesiredCapabilities()
    caps.setCapability("appPackage", "com.xueqiu.android")
    val driver=new RemoteWebDriver(new URL("http://127.0.0.1:8300/wd/hub"), caps)
    println(driver.getPageSource)
  }



  test("android driver"){
    //System.setProperty("webdriver.http.factory", "okhttp")
    val caps=new DesiredCapabilities()
    caps.setCapability("appPackage", "com.xueqiu.android")
    val driver= {
      new AndroidDriver[WebElement](new URL("http://127.0.0.1:8200/wd/hub"), caps)
    }
    println(driver.getPageSource)
  }
}
