package com.xueqiu.qa.appcrawler

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.{IOSMobileCapabilityType, AndroidMobileCapabilityType, MobileCapabilityType}
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import org.openqa.selenium.{OutputType, TakesScreenshot, WebElement}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest._
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}

import scala.sys.process.ProcessLogger
import scala.util.{Failure, Success, Try}

import scala.sys.process._
/**
  * Created by seveniruby on 16/3/26.
  */
class MiniAppium extends FunSuite
  with ShouldMatchers
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach with CommonLog {

  val capabilities = new DesiredCapabilities()
  var appiumUrl = "http://127.0.0.1:4723/wd/hub"

  implicit var driver: AppiumDriver[WebElement] = _
  var appiumProcess: Process = null
  var loc=""
  var index=0

  var screenWidth = 0
  var screenHeight = 0

  def start(port: Int=4723): Unit ={
    val buffer=new StringBuffer("\n")
    var lineBuffer=""
    val daemonLogger=ProcessLogger(line=>{
      buffer.append(line).append("\n")
      lineBuffer=line
    }, line=>{
      buffer.append(line).append("\n")
      lineBuffer=line
    })
    appiumProcess=Process(s"appium -p ${port}").run(daemonLogger)
    var waitTime=0
    def waitForStarted(): Unit ={
      waitTime+=1
      if(waitTime>10){
        return
      }
      sleep(0.5)
      if(buffer.toString.contains("started")){
        log.info(buffer)
      }else{
        waitForStarted()
      }
    }
    waitForStarted()
    log.info(buffer)
  }
  def stop(): Unit ={
    appiumProcess.destroy()
  }

  def config(key: String, value: Any): Unit = {
    capabilities.setCapability(key, value)
  }

  def sleep(seconds: Double = 1.0F): Unit = {
    Thread.sleep((seconds * 1000).toInt)
  }

  /**
    * 在5s的时间内确定元素存在并且位置是固定的
    *
    * @param key
    */
  def wait(key:String): Unit ={
    var isFound=false
    var preLocation:org.openqa.selenium.Point=null
    1 to 10 foreach (i=>{
      if(isFound==false) {
        log.info(s"find by xpath ${keyToXPath(key)}")
        val elements=driver.findElementsByXPath(keyToXPath(key))
        if (elements.size() > 0) {
          log.info("found")
          val curLocation=elements.get(0).getLocation
          if(preLocation==curLocation){
            log.info("ui ready")
            isFound=true
          }else{
            log.info("ui not ready")
          }
          preLocation=curLocation
          isFound = true
        } else {
          sleep(0.5)
        }
      }
    })

  }

  def see(key: String="//*", index: Int = 0): this.type = {
    loc=key
    this.index=index
    wait(key)
    this
  }

  def tap(): this.type ={
    click on (XPathQuery(tree(loc, index)("xpath").toString))
    this
  }
  def send(keys: String): this.type = {
    tap()
    driver.getKeyboard.sendKeys(keys)
    this
  }
  def attribute(key:String): String ={
    nodes().head.get(key).get.toString
  }
  def apply(key:String): String ={
    attribute(key)
  }
  def nodes(): List[Map[String, Any]] ={
    RichData.parseXPath(keyToXPath(loc), RichData.toXML(pageSource))
  }


  def save(): Unit = {
    index += 1
    captureTo(s"${index}.jpg")
  }

  def appium(url: String = "http://127.0.0.1:4723/wd/hub"): Unit = {
    appiumUrl = url
    //todo: 无法通过url来确定是否是android, 需要改进
    println(capabilities)
    println(capabilities.getCapability("app"))
    println(capabilities.getCapability("appActivity"))
    println(capabilities.getCapability("appPackage"))
    if(capabilities.getCapability("app")==null){
      config("app", "")
    }
    if(capabilities.getCapability("deviceName")==null || capabilities.getCapability("deviceName").toString.isEmpty){
      config("deviceName", "demo")
    }
    if (
      capabilities.getCapability("app").toString.matches(".*\\.apk$") ||
        capabilities.getCapability("appActivity") !=null ||
        capabilities.getCapability("appPackage") !=null
    ) {
      driver = new AndroidDriver[WebElement](new URL(appiumUrl), capabilities)
    } else {
      driver = new IOSDriver[WebElement](new URL(appiumUrl), capabilities)
    }

    getDeviceInfo
    log.info(s"capture dir = ${new File(".").getAbsolutePath}")
    setCaptureDir(".")
    implicitlyWait(Span(10, Seconds))
  }

  def keyToXPath(key: String): String = {
    key.charAt(0) match {
      case '/' => {
        key
      }
      case '(' => {
        key
      }
      case '^' => {
        s"//*[" +
          s"matches(@text, '$key') " +
          s"or matches(@resource-id, '$key') " +
          s"or matches(@content-desc, '$key') " +
          s"or matches(@name, '$key') " +
          s"or matches(@label, '$key') " +
          s"or matches(@value, '$key') " +
          s"or matches(name(), '$key') " +
          s"]"
      }
      case _ => {
        s"//*[" +
          s"contains(@text, '$key') " +
          s"or contains(@resource-id, '$key') " +
          s"or contains(@content-desc, '$key') " +
          s"or contains(@name, '$key') " +
          s"or contains(@label, '$key') " +
          s"or contains(@value, '$key') " +
          s"or contains(name(), '$key') " +
          s"]"
      }
    }
  }

  /**
    * 解析给定的xpath表达式或者text的定位标记 把节点的信息转换为map
    *
    * @param key
    * @return
    */
  def tree(key: String = "//*", index: Int = 0): Map[String, Any] = {
    log.info(s"find by key = ${key} index=${index}")
    val nodes = RichData.parseXPath(keyToXPath(key), RichData.toXML(pageSource))
    nodes.foreach(node => {
      log.debug(s"index=${nodes.indexOf(node)}")
      node.foreach(kv=>{
        log.debug(kv)
      })
    })
    val ret = nodes.lift(index).getOrElse(Map[String, Any]())
    log.info(s"ret = ${ret}")
    ret
  }

  //todo: not test
  def crawl(conf: String = "", resultDir: String = "", maxDepth:Int=1): Unit = {
    var crawler: Crawler = new Crawler
    driver.getClass.getSimpleName match {
      case "AndroidDriver" => {
        crawler = new AndroidCrawler
      }
      case "IOSDriver" => {
        crawler = new IOSCrawler
      }
      case _ => {
        log.warn("never heard this driver before")
      }
    }
    if (conf.nonEmpty) {
      crawler.loadConf(conf)
    }
    if (resultDir.nonEmpty) {
      crawler.conf.resultDir = resultDir
    }
    crawler.conf.startupActions.clear()
    crawler.log.setLevel(Level.TRACE)
    crawler.conf.maxDepth = maxDepth
    crawler.start(driver)

  }

  def getDeviceInfo(): Unit = {
    val size = driver.manage().window().getSize
    screenHeight = size.getHeight
    screenWidth = size.getWidth
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }

  def swipe(direction: String = "default"): Unit = {
    log.info(s"start swipe ${direction}")
    var startX = 0.0
    var startY = 0.0
    var endX = 0.0
    var endY = 0.0
    direction match {
      case "left" => {
        startX = 0.8
        startY = 0.7
        endX = 0.2
        endY = 0.7
      }
      case "right" => {
        startX = 0.2
        startY = 0.5
        endX = 0.8
        endY = 0.5
      }
      case "up" => {
        startX = 0.5
        startY = 0.8
        endX = 0.5
        endY = 0.2
      }
      case "down" => {
        startX = 0.5
        startY = 0.2
        endX = 0.5
        endY = 0.8
      }
      case _ => {
        startX = 0.8
        startY = 0.8
        endX = 0.2
        endY = 0.2
      }
    }


    doAppium(driver.swipe(
      (screenWidth * startX).toInt, (screenHeight * startY).toInt,
      (screenWidth * endX).toInt, (screenHeight * endY).toInt, 1000
    )
    )
    sleep(1)
  }


  def doAppium[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        Some(v)
      }
      case Failure(e) => {
        log.warn("message=" + e.getMessage)
        log.warn("cause=" + e.getCause)
        //log.trace(e.getStackTrace.mkString("\n"))
        None
      }
    }

  }

  def shot(fileName:String=""): Unit ={
    sleep(1)
    val xpath=tree(loc, index)("xpath").toString
    val location=driver.findElementByXPath(xpath).getLocation
    val size=driver.findElementByXPath(xpath).getSize
    val x=location.getX
    val y=location.getY
    val w=size.getWidth
    val h=size.getHeight

    log.info(s"x=${location.getX} y=${location.getY} w=${size.getWidth} h=${size.getHeight}")
    val file=(driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)
    FileUtils.copyFile(file, new File(fileName+".png"))
    val subImg = ImageIO.read(file).getSubimage(x, y, w, h)
    ImageIO.write(subImg, "png", file)
    FileUtils.copyFile(file, new File(fileName+".x.y.png"))
  }
}
