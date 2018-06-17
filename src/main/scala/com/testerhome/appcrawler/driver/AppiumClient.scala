package com.testerhome.appcrawler.driver

import java.awt.{BasicStroke, Color}
import java.io.File
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

import javax.imageio.ImageIO
import com.testerhome.appcrawler.{AppCrawler, CommonLog, DataObject, URIElement}
import com.testerhome.appcrawler._
import io.appium.java_client.{AppiumDriver, MobileElement, TouchAction}
import io.appium.java_client.android.{AndroidDriver, AndroidElement}
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.touch.{LongPressOptions, TapOptions, WaitOptions}
import io.appium.java_client.touch.offset.{ElementOption, PointOption}
import org.apache.log4j.Level
import org.openqa.selenium.{OutputType, Rectangle, TakesScreenshot, WebElement}
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}

import scala.sys.process._
import collection.JavaConverters._

/**
  * Created by seveniruby on 16/8/9.
  */
class AppiumClient extends ReactWebDriver{
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
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
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
      new AppiumTouchAction(driver, screenWidth, screenHeight).swipe(startX, startY, endX, endY)
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
      //todo: RasterFormatException: (y + height) is outside of Raster 横屏需要处理异常
      val subImg=img.getSubimage(0, 0, screenWidth, screenHeight)
      ImageIO.write(subImg, "png", new java.io.File(newImageName))
    }else{
      log.info(s"ImageIO.write newImageName ${newImageName}")
      ImageIO.write(img, "png", new java.io.File(newImageName))
    }
  }
  override def clickLocation(): Unit = {
    val point=currentURIElement.center()
    driver.performTouchAction(
      new TouchAction(driver).tap(
        TapOptions.tapOptions().withPosition(
          PointOption.point(point.x, point.y)))
    )
  }
  override def click(): this.type ={
    log.info(currentElement)
    currentElement.click()
    this
  }
  override def tap(): this.type = {
    new AppiumTouchAction(driver).tap(currentElement)
    this
  }

  override def longTap(): this.type = {
    driver.performTouchAction(
      (new TouchAction(driver)
        .longPress(
          LongPressOptions.longPressOptions()
            .withElement(ElementOption.element(currentElement))
            .withDuration(Duration.ofSeconds(2))
        ))
    )
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


  override def findElementsByURI(element: URIElement, findBy: String): List[AnyRef] = {
    //todo: 优化速度，个别时候定位可能超过10s
    //todo: 多种策略，使用findElement 使用xml直接分析location 生成平台特定的定位符

    element match {
      case id if element.id.nonEmpty && findBy=="default" =>{
        log.info(s"findElementsById ${element.id}")
        driver.findElementsById(element.id).asScala.toList
      }
      case name if element.name.nonEmpty && findBy=="default" => {
        log.info(s"findElementsByAccessibilityId ${element.name}")
        driver.findElementsByAccessibilityId(element.name).asScala.toList
      }
      case android if findBy=="android" => {
        val locator="new UiSelector().className(\"" + element.tag + "\").instance(" + element.instance + ")"
        log.info(s"findElementsByAndroidUIAutomator $locator")
        driver.asInstanceOf[AndroidDriver[WebElement]].findElementsByAndroidUIAutomator(locator).asScala.toList
      }
      case _ => {
        //todo: 生成原生定位符
        log.info(s"findElementsByXPath ${element.loc}")
        driver.findElementsByXPath(element.loc).asScala.toList
      }
    }
  }

  override def findElementByURI(element: URIElement, findBy:String): AnyRef = {
    currentElement=super.findElementByURI(element,findBy).asInstanceOf[WebElement]
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
