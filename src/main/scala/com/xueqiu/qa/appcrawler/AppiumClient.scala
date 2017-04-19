package com.xueqiu.qa.appcrawler

import java.awt.{BasicStroke, Color}
import java.io.File
import java.net.URL
import java.util.concurrent.{Callable, Executors, TimeUnit, TimeoutException}
import javax.imageio.ImageIO

import com.thoughtworks.selenium.webdriven.commands.KeyEvent
import io.appium.java_client.{AppiumDriver, MobileCommand}
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{OutputType, Rectangle, TakesScreenshot, WebElement}
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.{Seconds, Span}
import org.w3c.dom.Document

import scala.io.Source
import scala.sys.process.{ProcessLogger, _}
import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 16/8/9.
  */
class AppiumClient extends CommonLog with WebBrowser with WebDriver{
  Runtimes.init()
  var conf: CrawlerConf = _

  implicit var driver: AppiumDriver[WebElement] = _
  var appiumProcess: Process = null
  var loc = ""
  var index = 0
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


  def shell(command:String): Unit ={
    sys.props("os.name").toLowerCase match {
      case x if x contains "windows" => Seq("cmd", "/C") ++ command
      case _ => command
    }
  }

  //todo: 集成appium进程管理
  def start(port: Int = 4723): Unit = {
    appiumProcess = Process(s"appium --session-override -p ${port}").run()
    asyncTask(10){
      appiumProcess.exitValue()
    } match {
      case None=>{log.info("appium start success")}
      case Some(code)=>{log.error(s"appium failed with code ${code}")}
    }

  }

  override def stop(): Unit = {
    appiumProcess.destroy()
  }

  override def hideKeyboard(): Unit = {
    driver.hideKeyboard()
  }


  /**
    * 在5s的时间内确定元素存在并且位置是固定的
    *
    * @param key
    */
  def wait(key: String): Unit = {
    var isFound = false
    1 to 10 foreach (i => {
      if (isFound == false) {
        log.info(s"find by xpath ${keyToXPath(key)}")
        val elements = driver.findElementsByXPath(keyToXPath(key))
        if (elements.size() > 0) {
          isFound = true
        } else {
          sleep(0.5)
        }
      }
    })

  }

  def see(key: String = "//*", index: Int = 0): this.type = {
    loc = key
    this.index = index
    wait(key)
    this
  }

/*
  override def tap(): this.type = {
    click on (XPathQuery(tree(loc, index)("xpath").toString))
    this
  }
*/

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

  def attribute(key: String): String = {
    nodes().head.get(key).get.toString
  }

  def apply(key: String): String = {
    attribute(key)
  }

  def nodes(): List[Map[String, Any]] = {
    RichData.getListFromXPath(keyToXPath(loc), RichData.toDocument(getPageSource))
  }


  def save(): Unit = {
    index += 1
    captureTo(s"${index}.jpg")
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

  /**
    * 解析给定的xpath表达式或者text的定位标记 把节点的信息转换为map
    *
    * @param key
    * @return
    */
  def tree(key: String = "//*", index: Int = 0): Map[String, Any] = {
    log.info(s"find by key = ${key} index=${index}")
    val nodes = RichData.getListFromXPath(keyToXPath(key), RichData.toDocument(getPageSource))
    nodes.foreach(node => {
      log.debug(s"index=${nodes.indexOf(node)}")
      node.foreach(kv => {
        log.debug(kv)
      })
    })
    val ret = nodes.lift(index).getOrElse(Map[String, Any]())
    log.info(s"ret = ${ret}")
    ret
  }

  //todo: not test
  def crawl(conf: String = "", resultDir: String = "", maxDepth: Int = 1): Unit = {
    var crawler: Crawler = new Crawler

    crawler = new Crawler
    if (conf.nonEmpty) {
      crawler.loadConf(conf)
    }
    if (resultDir.nonEmpty) {
      crawler.conf.resultDir = resultDir
    }
    crawler.conf.startupActions.clear()
    crawler.log.setLevel(Level.TRACE)
    crawler.conf.maxDepth = maxDepth
    crawler.start(this)

  }

  override def getDeviceInfo(): Unit = {
    val size = driver.manage().window().getSize
    screenHeight = size.getHeight
    screenWidth = size.getWidth
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }


  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Option[_] = {
    if(screenHeight<=0){
      getDeviceInfo()
    }
    retry(driver.swipe(
      (screenWidth * startX).toInt, (screenHeight * startY).toInt,
      (screenWidth * endX).toInt, (screenHeight * endY).toInt, 1000
    )
    )
  }


  override def screenshot(): File = {
    (driver.asInstanceOf[TakesScreenshot]).getScreenshotAs(OutputType.FILE)
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


  def hello(action: String, number: Int = 0): Unit = {
    println(s"hello ${action} ${number}")
  }

/*
  def tap(x: Int = screenWidth / 2, y: Int = screenHeight / 2): Unit = {
    log.info("tap")
    driver.tap(1, x, y, 100)
    //driver.findElementByXPath("//UIAWindow[@path='/0/2']").click()
    //new TouchAction(driver).tap(x, y).perform()
  }*/

  override def tap(): this.type = {
    driver.tap(1, currentElement, 100)
    this
  }

  override def longTap(): this.type = {
    driver.tap(1, currentElement, 3000)
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
    var source: String = ""
    //获取页面结构, 最多重试3次
    1 to 3 foreach (i => {
      asyncTask(20)(driver.getPageSource) match {
        case Some(v) => {
          log.trace("get page source success")
          //todo: wda返回的不是标准的xml
          val xmlStr=v match {
            case json if json.trim.charAt(0)=='{' => {
              log.info("json format maybe from wda")
              DataObject.fromJson[Map[String, String]](v).getOrElse("value", "")
            }
            case xml if xml.trim.charAt(0)=='<' =>{
              log.info("xml format ")
              xml
            }
          }
          source = RichData.toPrettyXML(xmlStr)
          return source
        }
        case None => {
          log.trace("get page source error")
        }
      }
    })
    source
  }


  def monkey(): Unit = {
    val crawl = AppCrawler.crawler
    val monkeyEvents = crawl.conf.monkeyEvents
    val count = monkeyEvents.size
    val limits = AppCrawler.crawler.conf.monkeyRunTimeSeconds
    val record = new DataRecord
    while (record.intervalMS() / 1000 < limits) {
      val number = util.Random.nextInt(count)
      val code = monkeyEvents(number)
      event(code)
      record.append(code)
      val element = URIElement(crawl.currentUrl + "_Monkey", "", "", "", s"monkey_${code}")
      crawl.store.setElementClicked(element)
    }
  }


  //todo:优化查找方法
  //找到统一的定位方法就在这里定义, 找不到就分别在子类中重载定义
  override def findElementByUrlElement(element: URIElement): Boolean = {
    //为了加速去掉id定位, 测试表明比xpath竟然还慢
    /*
    log.info(s"find element by uid ${element}")
    if (element.id != "") {
      log.info(s"find by id=${element.id}")
      MiniAppium.doAppium(driver.findElementsById(element.id)) match {
        case Some(v) => {
          val arr = v.toArray().distinct
          if (arr.length == 1) {
            log.trace("find by id success")
            return Some(arr.head.asInstanceOf[WebElement])
          } else {
            //有些公司可能存在重名id
            arr.foreach(log.info)
            log.info(s"find count ${arr.size}, change to find by xpath")
          }
        }
        case None => {
          log.warn("find by id error")
        }
      }
    }
    */
    //todo: 用其他定位方式优化
    log.info(s"find by xpath= ${element.loc}")
    retry(driver.findElementsByXPath(element.loc)) match {
      case Some(v) => {
        val arr = v.toArray().distinct
        arr.length match {
          case len if len == 1 => {
            log.info("find by xpath success")
            currentElement=arr.head.asInstanceOf[WebElement]
            return true
          }
          case len if len > 1 => {
            log.warn(s"find count ${v.size()}, you should check your dom file")
            //有些公司可能存在重名id
            arr.foreach(log.info)
            log.warn("just use the first one")
            currentElement=arr.head.asInstanceOf[WebElement]
            return true
          }
          case len if len == 0 => {
            log.warn("find by xpath error no element found")
          }
        }
      }
      case None => {
        log.warn("find by xpath error")
      }
    }
    false
  }


  override def getAppName(): String = {
    driver match {
      case android: AndroidDriver[_] => {
        val xpath="(//*[@package!=''])[1]"
        RichData.getListFromXPath(xpath, currentPageDom).head.getOrElse("package", "").toString
      }
      case ios: IOSDriver[_] => {
        val xpath="//*[contains(name(), 'Application')]"
        RichData.getListFromXPath(xpath, currentPageDom).head.getOrElse("name", "").toString
      }
    }

  }

  override def getUrl(): String = {
    driver match {
      case android: AndroidDriver[_] => {
        (asyncTask() {
          driver.asInstanceOf[AndroidDriver[WebElement]].currentActivity()
        }).getOrElse("").split('.').last
      }
      case ios: IOSDriver[_] => {
        val xpath="//*[contains(name(), 'NavigationBar')]"
        RichData.getListFromXPath(xpath, currentPageDom).map(_.getOrElse("name", "").toString).mkString("")
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



}
object AppiumClient extends AppiumClient
