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

  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    log.addAppender(AppCrawler.crawler.fileAppender)
    appium(url, configMap)
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

    //todo: 支持Selenium
    capabilities.asMap().keySet() match {
      case android if android.contains("appPackage")
        | capabilities.getCapability("app").toString.trim.takeRight(4).contains(".apk")  =>{
        platformName="Android"
        config("platformName", platformName)
        driver = new AndroidDriver[WebElement](new URL(url), capabilities)
      }
      case ios if ios.contains("bundleId")
        | ios.contains("udid")
        | capabilities.getCapability("app").toString.trim.takeRight(4).contains(".ipa")
        | capabilities.getCapability("app").toString.trim.takeRight(4).contains(".app")  =>{
        platformName="iOS"
        config("platformName", platformName)
        driver = new IOSDriver[WebElement](new URL(url), capabilities)
      }
    }

    getDeviceInfo()
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
      case id if element.id.nonEmpty && findBy=="id" =>{
        log.info(s"findElementsById ${element.id}")
        driver.findElementsById(element.id).asScala.toList
      }
      case name if element.name.nonEmpty && findBy=="accessibilityId" => {
        log.info(s"findElementsByAccessibilityId ${element.name}")
        driver.findElementsByAccessibilityId(element.name).asScala.toList
      }
      case android if platformName.toLowerCase=="android" && findBy=="default" => {
        val locator=new StringBuilder()
        locator.append("new UiSelector()")
        locator.append(".className(\"" + element.tag + "\")")
        if(element.text.nonEmpty){
          locator.append(".text(\"" + element.text + "\")" )
        }
        if(element.id.nonEmpty){
          locator.append(".resourceId(\"" + element.id + "\")")
        }
        if(element.name.nonEmpty){
          locator.append(".description(\"" + element.name + "\")" )
        }
        if(element.instance.nonEmpty && element.text.isEmpty && element.name.isEmpty && element.id.isEmpty){
          //todo: 如果出现动态出现的控件会影响定位
          //todo: webview内的元素貌似用instance是不行的，webview内的真实的instance与appium给的不一致
          locator.append(".instance(" + element.instance + ")" )
        }
        log.info(s"findElementByAndroidUIAutomator ${locator.toString()}")
        asyncTask(){
          List(driver.asInstanceOf[AndroidDriver[WebElement]].findElementByAndroidUIAutomator(locator.toString()))
        } match {
          case Left(value)=>{
            value
          }
          case Right(value)=>{
            log.warn(s"findElementByAndroidUIAutomator error, use findElementsByAndroidUIAutomator ${locator.toString()}")
            driver.asInstanceOf[AndroidDriver[WebElement]].findElementsByAndroidUIAutomator(locator.toString()).asScala.toList
          }
        }
        //driver.asInstanceOf[AndroidDriver[WebElement]].findElementsByAndroidUIAutomator(locator.toString()).asScala.toList
        //todo: 个别时候findElement会报错而实际上控件存在，跟uiautomator的定位算法有关
        //todo: 结合findElement和findElements
        //todo: 修复appium bug
        //List(driver.asInstanceOf[AndroidDriver[WebElement]].findElementByAndroidUIAutomator(locator.toString()))
      }
      case _ => {
        //todo: 生成iOS原生定位符
        //默认使用xpath
        log.info(s"findElementsByXPath ${element.xpath}")
        //driver.findElementsByXPath(element.xpath).asScala.toList
        List(driver.findElementByXPath(element.xpath))
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
        getNodeListByKey(xpath).headOption.getOrElse(Map("package"->"")).get("package").getOrElse("").toString
      }
      case ios: IOSDriver[_] => {
        val xpath="//*[contains(name(), 'Application')]"
        getNodeListByKey(xpath).head.getOrElse("name", "").toString
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
        getNodeListByKey(xpath).map(_.getOrElse("name", "").toString).mkString("")
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
