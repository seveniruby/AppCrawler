package com.testerhome.appcrawler.driver

import java.io.File
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

import com.testerhome.appcrawler.{AppCrawler, URIElement, _}
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.touch.offset.{ElementOption, PointOption}
import io.appium.java_client.touch.{LongPressOptions, TapOptions}
import io.appium.java_client.{AppiumDriver, MobileElement, TouchAction}

import scala.collection.JavaConverters._

/**
  * Created by seveniruby on 16/8/9.
  */
class AppiumClient extends SeleniumDriver {
  Util.init()

  var androidDriver: AndroidDriver[MobileElement] = _
  var iosDriver: IOSDriver[MobileElement] = _
  var appiumDriver: AppiumDriver[MobileElement] = _


  def this(url: String = "http://127.0.0.1:4723/wd/hub", configMap: Map[String, Any]=Map[String, Any]()) {
    this
    log.addAppender(AppCrawler.fileAppender)

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
        androidDriver = new AndroidDriver[MobileElement](new URL(url), capabilities)
        appiumDriver=androidDriver
        driver=appiumDriver
      }
      case ios if ios.contains("bundleId")
        | ios.contains("udid")
        | capabilities.getCapability("app").toString.trim.takeRight(4).contains(".ipa")
        | capabilities.getCapability("app").toString.trim.takeRight(4).contains(".app")  =>{
        platformName="iOS"
        config("platformName", platformName)
        iosDriver = new IOSDriver[MobileElement](new URL(url), capabilities)
        appiumDriver=iosDriver
        driver=appiumDriver
      }
    }

    getDeviceInfo()
    log.info(s"capture dir = ${new File(".").getAbsolutePath}")
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)

  }


  override def event(keycode: Int): Unit = {
    driver match {
      case android: AndroidDriver[_] => {
        log.info(s"send event ${keycode}")
        androidDriver.pressKeyCode(keycode)
      }
      case ios: IOSDriver[_] => {
        log.error("no event for ios")
      }
    }
  }


  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Unit = {
    if(screenHeight<=0){
      getDeviceInfo()
    }
    asyncTask()(
      new AppiumTouchAction(appiumDriver, screenWidth, screenHeight).swipe(startX, startY, endX, endY)
    )
  }


  override def clickLocation(): Unit = {
    val point=currentURIElement.center()
    appiumDriver.performTouchAction(
      new TouchAction(appiumDriver).tap(
        TapOptions.tapOptions().withPosition(
          PointOption.point(point.x, point.y)))
    )

  }

  override def tap(): this.type = {
    new AppiumTouchAction(appiumDriver).tap(currentElement)
    this
  }

  override def longTap(): this.type = {
    appiumDriver.performTouchAction(
      (new TouchAction(appiumDriver)
        .longPress(
          LongPressOptions.longPressOptions()
            .withElement(ElementOption.element(currentElement))
            .withDuration(Duration.ofSeconds(2))
        ))
    )
    this
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
        appiumDriver.findElementsByAccessibilityId(element.name).asScala.toList
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
          List(androidDriver.findElementByAndroidUIAutomator(locator.toString()))
        } match {
          case Left(value)=>{
            value
          }
          case Right(value)=>{
            log.warn(s"findElementByAndroidUIAutomator error, use findElementsByAndroidUIAutomator ${locator.toString()}")
            androidDriver.findElementsByAndroidUIAutomator(locator.toString()).asScala.toList
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
          androidDriver.currentActivity()
        }).left.getOrElse("").split('.').last
      }
      case ios: IOSDriver[_] => {
        val xpath="//*[contains(name(), 'NavigationBar')]"
        getNodeListByKey(xpath).map(_.getOrElse("name", "").toString).mkString("")
      }
    }
  }


  override def launchApp(): Unit = {
    appiumDriver.launchApp()
  }

  override def getPageSource(): String = {
    appiumDriver.getPageSource
  }

}
object AppiumClient extends AppiumClient
