package com.testerhome.appcrawler.driver

import java.awt.{BasicStroke, Color}
import java.io.File
import java.net.URL
import java.time.Duration
import javax.imageio.ImageIO

import com.testerhome.appcrawler.{AppCrawler, CommonLog, DataObject, URIElement}
import com.testerhome.appcrawler._
import io.appium.java_client.{AppiumDriver, TouchAction}
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import org.apache.log4j.Level
import org.openqa.selenium.{OutputType, Rectangle, TakesScreenshot, WebElement}
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}

import scala.sys.process._
import collection.JavaConverters._

/**
  * Created by seveniruby on 16/8/9.
  */
class AppiumClient extends WebBrowser with WebDriver{
  Util.init()
  var conf: CrawlerConf = _

  implicit var driver: AppiumDriver[WebElement] = _
  var appiumProcess: Process = null

  var currentElement:WebElement=_

  private var platformName = ""

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    appium(url, configMap)
  }

  def setPlatformName(platform: String): Unit = {
    log.info(s"set platform ${platform}")
    platformName = platform
  }


  //todo: 集成appium进程管理
  def start(port: Int = 4723): Unit = {
    appiumProcess = Process(s"appium --session-override -p ${port}").run()
    asyncTask(10){
      appiumProcess.exitValue()
    } match {
      case Left(x)=>{log.info("appium start success")}
      case Right(code)=>{log.error(s"appium failed with code ${code}")}
    }

  }

  override def stop(): Unit = {
    appiumProcess.destroy()
  }

  override def hideKeyboard(): Unit = {
    driver.hideKeyboard()
  }


  def send(keys: String): this.type = {
    tap()
    driver.getKeyboard.sendKeys(keys)
    this
  }

  override def event(keycode: Int): Unit = {
    driver match {
      case androidDriver: AndroidDriver[WebElement] => {
        log.info(s"send event ${keycode}")
        androidDriver.pressKeyCode(keycode)
      }
      case iosDriver: IOSDriver[_] => {
        log.error("no event for ios")
      }
    }
  }


  def appium(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()): Unit = {
    configMap.foreach(c=>config(c._1, c._2))
    //todo: 无法通过url来确定是否是android, 需要改进
    if (capabilities.getCapability("app") == null) {
      config("app", "")
    }
    if (capabilities.getCapability("deviceName") == null || capabilities.getCapability("deviceName").toString.isEmpty) {
      config("deviceName", "demo")
    }
    if (
      capabilities.getCapability("app").toString.matches(".*\\.apk$") ||
        capabilities.getCapability("appActivity") != null ||
        capabilities.getCapability("appPackage") != null
    ) {
      driver = new AndroidDriver[WebElement](new URL(url), capabilities)
      setPlatformName("android")
    } else {
      driver = new IOSDriver[WebElement](new URL(url), capabilities)
      setPlatformName("ios")
    }

    getDeviceInfo
    log.info(s"capture dir = ${new File(".").getAbsolutePath}")
    setCaptureDir(".")
    implicitlyWait(Span(10, Seconds))
  }

  override def getDeviceInfo(): Unit = {
    val size = driver.manage().window().getSize
    screenHeight = size.getHeight
    screenWidth = size.getWidth
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }


  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Unit = {
    if(screenHeight<=0){
      getDeviceInfo()
    }
    asyncTask()(
      driver.performTouchAction(
        new TouchAction(driver)
          .press((screenWidth * startX).toInt, (screenHeight * startY).toInt)
          .waitAction(Duration.ofSeconds(1))
          //.moveTo((screenWidth * (endX-startX)).toInt, (screenHeight * (endY-startY)).toInt)
          .moveTo((screenWidth * endX).toInt, (screenHeight * endY).toInt)
          .release()
      )
    )
  }


  override def screenshot(): File = {
    (driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)
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
      val subImg=img.getSubimage(0, 0, screenWidth, screenHeight)
      ImageIO.write(subImg, "png", new java.io.File(newImageName))
    }else{
      ImageIO.write(img, "png", new java.io.File(newImageName))
    }
  }
  override def clickLocation(): Unit = {
    val point=currentURIElement.center()
    driver.performTouchAction(new TouchAction(driver).tap(point.x, point.y))
  }
  override def tap(): this.type = {
    driver.performTouchAction(new TouchAction(driver).tap(currentElement))
    this
  }

  override def longTap(): this.type = {
    driver.performTouchAction(new TouchAction(driver).longPress(currentElement))
    this
  }

  override def back(): Unit = {
    log.info("navigate back")
    driver.navigate().back()
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
    driver.getPageSource
  }


  override def findElementsByURI(element: URIElement): List[AnyRef] = {
    driver.findElementsByXPath(element.loc).asScala.toList
  }

  override def findElementByURI(element: URIElement): AnyRef = {
    currentElement=super.findElementByURI(element).asInstanceOf[WebElement]
    currentElement
  }

  override def getAppName(): String = {
    driver match {
      case android: AndroidDriver[_] => {
        val xpath="(//*[@package!=''])[1]"
        findMapByKey(xpath).headOption.getOrElse(Map("package"->"")).get("package").getOrElse("").toString
      }
      case ios: IOSDriver[_] => {
        val xpath="//*[contains(name(), 'Application')]"
        findMapByKey(xpath).head.getOrElse("name", "").toString
      }
    }

  }

  override def getUrl(): String = {
    driver match {
      case android: AndroidDriver[_] => {
        (asyncTask() {
          //todo: 此api不稳定，会导致appium在执行几百次api后发生异常
          driver.asInstanceOf[AndroidDriver[WebElement]].currentActivity()
        }).left.getOrElse("").split('.').last
      }
      case ios: IOSDriver[_] => {
        val xpath="//*[contains(name(), 'NavigationBar')]"
        findMapByKey(xpath).map(_.getOrElse("name", "").toString).mkString("")
      }
    }
  }

  override def getRect(): Rectangle ={
    val location=currentElement.getLocation
    val size=currentElement.getSize
    new Rectangle(location.x, location.y, size.height, size.width)
  }

  override def sendKeys(content: String): Unit = {
    currentElement.sendKeys(content)
  }

  override def launchApp(): Unit = {
    driver.launchApp()
  }


}
object AppiumClient extends AppiumClient
