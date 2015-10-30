/**
 * Created by seveniruby on 15/10/15.
 */

//package org.scalatest.selenium

package io.appium.java_client


import java.net.URL

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{By, WebDriver, Capabilities, WebElement}
import org.scalatest.selenium.WebBrowser
import org.scalatest.selenium.WebBrowser.Query

import scala.reflect.macros.Context

/*
sealed trait Query {
  def findElement(implicit driver: WebDriver): Option[org.scalatest.selenium.WebBrowser.Element] =
    try {
      Some(createTypedElement(driver.findElement(by)))
    }
    catch {
      case e: org.openqa.selenium.NoSuchElementException => None
    }


}
*/

import scala.reflect.runtime.universe._

/*

object XueqiuDriver{
  var xueqiuDriver:XueqiuDriver[WebElement]=_
  implicit def AndroidDriver2XueqiuDriver(driver: AndroidDriver[WebElement]): XueqiuDriver[WebElement] = {
    println("replace with xueqiu driver")
    this.xueqiuDriver=new XueqiuDriver[WebElement]()
    this.xueqiuDriver.driver=driver
    this.xueqiuDriver
  }

  def main(args: Array[String]) {
    val capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", "emulator-5554");
    capabilities.setCapability("platformVersion", "4.4");
    capabilities.setCapability("appPackage", "com.xueqiu.android");
    capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "com.xueqiu.android.view.WelcomeActivityAlias")
    //capabilities.setCapability("appActivity", ".ApiDemos");
    capabilities.setCapability("autoLaunch", "true")
    capabilities.setCapability("automationName", "Selendroid")
    capabilities.setCapability(MobileCapabilityType.APP, "/Users/seveniruby/Downloads/xueqiu.apk")

    val driver=new AndroidDriver[WebElement](new URL("http://127.0.0.1:4723/wd/hub"), capabilities)
    driver.update()
  }
}

class XueqiuDriver[T <:WebElement] {
  var driver:AndroidDriver[T]=_
  def update(): Unit ={
    typeOf[AndroidDriver[WebElement]].decls.foreach(m=>{
      println(m.name)
    })

    val rm=runtimeMirror(getClass.getClassLoader)
    val im=rm.reflect(this.driver)
    val methodx=driver.getClass.getDeclaredMethod("findElement", classOf[org.openqa.selenium.By])
    methodx.setAccessible(true)
    val method=typeOf[AndroidDriver[WebElement]].decl(TermName("findElement")).asMethod
    val m=im.reflectMethod(method)
    //因为权限无法clone
    m.clone()
    println(m)
    println(m.clone())

  }
}
*/

trait XueqiuDriverFind[T <: WebElement] extends AndroidDriver[T] {
  override def findElement(by: By): T = {
    println("trait invoke find element")
    println(this.getClass)
    println(super.getClass)
    val res=super.findElement(by)
    return res
  }
}

class XueqiuDriver[T <: WebElement](remoteAddress: URL, desiredCapabilities: Capabilities)
  extends AndroidDriver[T](remoteAddress, desiredCapabilities) with XueqiuDriverFind[T] {

}

/*

trait WebBrowser {
  sealed trait Query {
    override def findElement(implicit driver: WebDriver): Option[WebBrowser.Element] = {
      println("xueqiu find element")
      try {
        Some(createTypedElement(driver.findElement(by)))
      }
      catch {
        case e: org.openqa.selenium.NoSuchElementException => None
      }

    }
  }

}
*/

