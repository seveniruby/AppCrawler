package com.testerhome.appcrawler.it

import java.net.URL

import com.sun.jdi.connect.spi.TransportService.Capabilities
import com.testerhome.appcrawler.driver.AppiumClient
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
  test("appium success"){
    a.start()
    println(Source.fromURL("http://127.0.0.1:4723/wd/hub/sessions").mkString)
    a.stop()
  }

  test("single session"){
    val capa=new DesiredCapabilities()
    capa.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.xueqiu.android")
    capa.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".view.WelcomeActivityAlias")
    capa.setCapability(MobileCapabilityType.DEVICE_NAME, "demo")
    val driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub/"), capa)


  }
}
