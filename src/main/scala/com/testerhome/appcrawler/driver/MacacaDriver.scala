package com.testerhome.appcrawler.driver

import java.awt.{BasicStroke, Color}
import java.io.File
import javax.imageio.ImageIO

import com.alibaba.fastjson.JSONObject
import com.testerhome.appcrawler.{AppCrawler, CommonLog, DataObject, URIElement}
import com.testerhome.appcrawler._
import macaca.client.MacacaClient
import org.apache.log4j.Level
import org.openqa.selenium.Rectangle
import org.scalatest.selenium.WebBrowser

import scala.sys.process._

/**
  * Created by seveniruby on 16/8/9.
  */
class MacacaDriver extends ReactWebDriver{
  Util.init()
  var conf: CrawlerConf = _

  implicit var driver: MacacaClient = _
  var appiumProcess: Process = null
  var currentElement: macaca.client.commands.Element =_

  private var platformName = ""

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    appium(url, configMap)
  }

  def appium(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()): Unit = {
    driver=new MacacaClient()
    val porps = new JSONObject()
    configMap.foreach(m=>porps.put(m._1,  m._2))
    porps.put("package", configMap("appPackage"))
    porps.put("activity", configMap("appActivity"))

    val desiredCapabilities = new JSONObject()
    desiredCapabilities.put("desiredCapabilities", porps)
    driver.initDriver(desiredCapabilities)

    getDeviceInfo
  }


  override def stop(): Unit = {
    appiumProcess.destroy()
  }

  override def hideKeyboard(): Unit = {
    //todo:
  }


/*
  override def tap(): this.type = {
    click on (XPathQuery(tree(loc, index)("xpath").toString))
    this
  }
*/

  override def event(keycode: Int): Unit = {
    //todo:
    log.error("not implete")
  }


  override def getDeviceInfo(): Unit = {
    val size=driver.getWindowSize
    log.info(s"size=${size}")
    screenHeight = size.get("height").toString.toInt
    screenWidth = size.get("width").toString.toInt
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }

  override def swipe(startX: Double = 0.9, endX: Double = 0.1, startY: Double = 0.9, endY: Double = 0.1): Unit = {
    //macaca 2.0.20有api变动
    asyncTask() {
      val json = new JSONObject()
      json.put("fromX", (screenWidth * startX).toInt)
      json.put("fromY", (screenHeight * startY).toInt)
      json.put("toX", (screenWidth * endX).toInt)
      json.put("toY", (screenHeight * endY).toInt)
      json.put("duration", 2)
      driver.touch("drag", json)
    }

  }


  override def screenshot(): File = {
    val location="/tmp/1.png"
    driver.saveScreenshot(location)
    new File(location)
  }

  //todo: 重构到独立的trait中
  override def mark(fileName: String, newImageName:String,  x: Int, y: Int, w: Int, h: Int): Unit = {
    val file = new java.io.File(fileName)
    log.info(s"platformName=${platformName}")
    log.info("getScreenshot")
    val img = ImageIO.read(file)
    val graph = img.createGraphics()

    if (platformName.toLowerCase == "ios") {
      log.info("scale the origin image")
      graph.drawImage(img, 0, 0, screenWidth, screenHeight, null)
    }
    graph.setStroke(new BasicStroke(5))
    graph.setColor(Color.RED)
    graph.drawRect(x, y, w, h)
    graph.dispose()

    log.info(s"write png ${fileName}")
    if (platformName.toLowerCase == "ios") {
      val subImg=img.getSubimage(0, 0, screenWidth, screenHeight)
      ImageIO.write(subImg, "png", new java.io.File(newImageName))
    } else {
      ImageIO.write(img, "png", new java.io.File(newImageName))
    }
  }


/*
  def tap(x: Int = screenWidth / 2, y: Int = screenHeight / 2): Unit = {
    log.info("tap")
    driver.tap(1, x, y, 100)
    //driver.findElementByXPath("//UIAWindow[@path='/0/2']").click()
    //new TouchAction(driver).tap(x, y).perform()
  }*/

  //todo: 用真正的tap替代
  override def tap(): this.type = {
    currentElement.click()
    this
  }
  override def click(): this.type = {
    currentElement.click()
    this
  }

  override def longTap(): this.type = {
    currentElement.click()
    this
  }

  override def back(): Unit = {
    log.info("navigate back")
    driver.back()
  }

  override def backApp(): Unit = {
    /*
    sleep(10)
    event(AndroidKeyCode.BACK)
    sleep(2)
    event(AndroidKeyCode.ENTER)
    */
    back()
  }

  override def getPageSource(): String = {
    driver.source()
  }

  override def findElementsByURI(element: URIElement, findBy:String): List[AnyRef] = {
    //todo: 改进macaca定位
    val s=driver.elementsByXPath(element.loc)
    0 until s.size() map(s.getIndex(_)) toList
  }

  override def findElementByURI(element: URIElement, findBy:String): AnyRef = {
    currentElement=super.findElementByURI(element, findBy).asInstanceOf[macaca.client.commands.Element]
    currentElement
  }

  override def getAppName(): String = {
    val xpath="(//*[@package!=''])[1]"
    findMapByKey(xpath).head.getOrElse("package", "").toString
  }

  override def getUrl(): String = {
    //todo: macaca的url没设定
    //driver.title()
    ""
  }

  override def getRect(): Rectangle ={
    val rect=currentElement.getRect.asInstanceOf[JSONObject]
    new Rectangle(rect.getIntValue("x"), rect.getIntValue("y"), rect.getIntValue("height"), rect.getIntValue("width"))
  }


  override def sendKeys(content: String): Unit = {
    currentElement.sendKeys(content)
  }



}

