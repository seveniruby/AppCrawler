package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler._
import com.ceshiren.appcrawler.core.CrawlerConf
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.DynamicEval
import org.openqa.selenium.Rectangle

import java.awt.{BasicStroke, Color}
import java.io.File
import javax.imageio.ImageIO
import scala.io.Source
import scala.sys.process._

/**
  * Created by seveniruby on 18/10/31.
  * 用于测试用途
  */
class MockDriver extends ReactWebDriver {
  var conf: CrawlerConf = _

  var packageName = ""
  var activityName = ""

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any] = Map[String, Any]()) {
    this
    log.info(s"url=${url}")
    packageName = configMap.getOrElse("appPackage", "").toString
    activityName = configMap.getOrElse("appActivity", "").toString
  }


  override def event(keycode: String): Unit = {
    log.error("not implement")
  }

  //todo: outside of Raster 问题
  override def getDeviceInfo(): Unit = {
    log.error("not implement")
  }


  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Unit = {
    log.error("not implement")
  }


  override def screenshot(): File = {
    val file = File.createTempFile("tmp", ".png")
    log.info(file.getAbsolutePath)
    file
  }

  override def click(): this.type = {
    this
  }

  override def tap(): this.type = {
    click()
  }
  override def tapLocation(x: Int, y: Int): this.type = {
    this
  }
  override def longTap(): this.type = {
    log.error("not implement")
    this
  }

  override def back(): Unit = {
  }

  override def backApp(): Unit = {
  }

  override def getPageSource(): String = {
    Source.fromFile("src/test/scala/com/ceshiren/appcrawler/ut/miniprogram.xml").mkString
  }

  override def getAppName(): String = {
    "com.ceshiren.appcrawler.mockapp"
  }

  override def getUrl(): String = {
    "DemoActivity"
  }

  override def getRect(): Rectangle = {
    //selenium下还没有正确的赋值，只能通过api获取
    null
  }

  override def sendKeys(content: String): Unit = {
  }

  override def launchApp(): Unit = {
    //driver.get(capabilities.getCapability("app").toString)
    back()
  }

  override def findElements(element: URIElement, findBy: String): List[AnyRef] = {
    List(element)
  }

  def getAdb(): String ={
    List(System.getenv("ANDROID_HOME"), "platform-tools/adb").mkString(File.separator)
  }
  override def reStartDriver(): Unit ={
  }
  def shell(cmd:String): String ={
    log.info(cmd)
    val result=cmd.!!
    log.info(result)
    result
  }

}

