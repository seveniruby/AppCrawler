package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.data.AbstractElement
import com.ceshiren.appcrawler.{AppCrawler, CrawlerConf}

import java.awt.{BasicStroke, Color}
import java.io.File
import com.ceshiren.appcrawler._

import javax.imageio.ImageIO
import org.openqa.selenium.Rectangle

import scala.sys.process._

/**
  * Created by seveniruby on 18/10/31.
  * 用于测试用途
  */
class MockDriver extends ReactWebDriver{
  DynamicEval.init()
  var conf: CrawlerConf = _
  val adb=getAdb()

  var packageName=""
  var activityName=""

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    log.addAppender(AppCrawler.fileAppender)
    log.info(s"url=${url}")


    packageName=configMap.getOrElse("appPackage", "").toString
    activityName=configMap.getOrElse("appActivity", "").toString

    if(configMap.getOrElse("noReset", "").toString.equals("false")){
      shell(s"${adb} shell pm clear ${packageName}")
    }else{
      log.info("need need to reset app")
    }
    shell(s"${adb} shell am start -W -n ${packageName}/${activityName}")
  }


  override def event(keycode: Int): Unit = {
    log.error("not implement")
  }

  //todo: outside of Raster 问题
  override def getDeviceInfo(): Unit = {
    val size=shell(s"${adb} shell wm size").split(' ').last.split('x')
    screenHeight = size.last.trim.toInt
    screenWidth = size.head.trim.toInt
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }


  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Unit = {
    log.error("not implement")
  }


  override def screenshot(): File = {
    val file=File.createTempFile("tmp", ".png")
    log.info(file.getAbsolutePath)
    val cmd=s"${adb} exec-out screencap -p"
    log.info(cmd)
    (cmd #> file).!!
    file
  }

  //todo: 重构到独立的trait中
  override def mark(fileName: String, newImageName:String,  x: Int, y: Int, w: Int, h: Int): Unit = {
    val file = new java.io.File(fileName)
    log.info(s"read from ${fileName}")
    val img = ImageIO.read(file)
    val graph = img.createGraphics()

    if(img.getWidth>screenWidth){
      log.info("scale the origin image")
      graph.drawImage(img, 0, 0, screenWidth, screenHeight, null)
    }
    graph.setStroke(new BasicStroke(5))
    graph.setColor(Color.RED)
    graph.drawRect(x, y, w, h)
    graph.dispose()

    log.info(s"write png ${fileName}")
    if(img.getWidth>screenWidth){
      log.info("scale the origin image and save")
      //fixed: RasterFormatException: (y + height) is outside of Raster 横屏需要处理异常
      val subImg=tryAndCatch(img.getSubimage(0, 0, screenWidth, screenHeight)) match {
        case Some(value)=>value
        case None => {
          getDeviceInfo()
          img.getSubimage(0, 0, screenWidth, screenHeight)
        }
      }
      ImageIO.write(subImg, "png", new java.io.File(newImageName))
    }else{
      log.info(s"ImageIO.write newImageName ${newImageName}")
      ImageIO.write(img, "png", new java.io.File(newImageName))
    }
  }
  override def click(): this.type ={
    val center=currentURIElement.center()
    shell(s"${adb} shell input tap ${center.x} ${center.y}")
    this
  }
  override def tap(): this.type = {
    click()
  }

  override def longTap(): this.type = {
    log.error("not implement")
    this
  }

  override def back(): Unit = {
    shell(s"${adb} shell input keyevent 4")
  }

  override def backApp(): Unit = {
    shell(s"${adb} shell am start -W -n ${packageName}/${activityName}")
  }

  override def getPageSource(): String = {
    //todo: null root问题, idle wait timeout问题, Uiautomator dump太鸡肋了，没啥用, https://github.com/appium/appium-uiautomator2-server/pull/80
    shell(
      s"""${adb} shell eval "[ -f /data/local/tmp/source.xml ] && rm /data/local/tmp/source.xml >/dev/null 2>&1 ;
         |uiautomator dump /data/local/tmp/source.xml > /dev/null;
         |cat /data/local/tmp/source.xml || echo ERROR " """.stripMargin)
  }

  override def getAppName(): String = {
    val appName=shell(s"${adb} shell dumpsys window windows | grep mFocusedApp=").split('/').head.split(' ').last
    if(appName.contains("=null")){
      shell(s"${adb} shell dumpsys window windows")
      System.exit(1)
      appName
    }else{
      appName
    }
  }

  override def getUrl(): String = {
    shell(s"${adb} shell dumpsys window windows | grep mFocusedApp=").split('/').last.split(' ').head
  }

  override def getRect(): Rectangle ={
    //selenium下还没有正确的赋值，只能通过api获取
    if(currentURIElement.getHeight!=0){
      //log.info(s"location=${location} size=${size} x=${currentURIElement.x} y=${currentURIElement.y} width=${currentURIElement.width} height=${currentURIElement.height}" )
      new Rectangle(currentURIElement.getX, currentURIElement.getY, currentURIElement.getHeight, currentURIElement.getWidth)
    }else {
      log.error("rect height < 0")
      return null
    }
  }

  override def sendKeys(content: String): Unit = {
    tap()
    shell(s"${adb} shell input text ${content}")
  }

  override def launchApp(): Unit = {
    //driver.get(capabilities.getCapability("app").toString)
    back()
  }

  override def findElementsByURI(element: AbstractElement, findBy: String): List[AnyRef] = {
    List(element)
  }

  def getAdb(): String ={
    List(System.getenv("ANDROID_HOME"), "platform-tools/adb").mkString(File.separator)
  }

  def shell(cmd:String): String ={
    log.info(cmd)
    val result=cmd.!!
    log.info(result)
    result
  }

}

