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
class MacacaDriver extends CommonLog with WebBrowser with WebDriver{
  Runtimes.init()
  var conf: CrawlerConf = _

  implicit var driver: MacacaClient = _
  var appiumProcess: Process = null
  var loc = ""
  var index = 0
  var currentElement: macaca.client.commands.Element =_

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
    log.error("TODO")
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
    //todo:
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
        val elements = driver.elementsByXPath(key)
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

  override def event(keycode: Int): Unit = {
    //todo:
    log.error("not implete")
  }

  def attribute(key: String): String = {
    nodes().head.get(key).get.toString
  }

  def apply(key: String): String = {
    attribute(key)
  }

  def nodes(): List[Map[String, Any]] = {
    getListFromXPath(loc)
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

  /**
    * 解析给定的xpath表达式或者text的定位标记 把节点的信息转换为map
    *
    * @param key
    * @return
    */
  def tree(key: String = "//*", index: Int = 0): Map[String, Any] = {
    log.info(s"find by key = ${key} index=${index}")
    val nodes = getListFromXPath(key)
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
    crawler.log.setLevel(Level.TRACE)
    crawler.conf.maxDepth = maxDepth
    crawler.start(this)

  }

  override def getDeviceInfo(): Unit = {
    val size=driver.getWindowSize
    log.info(s"size=${size}")
    screenHeight = size.get("height").toString.toInt
    screenWidth = size.get("width").toString.toInt
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }

  override def swipe(startX: Double = 0.9, endX: Double = 0.1, startY: Double = 0.9, endY: Double = 0.1): Option[_] = {
    retry(driver.swipe(
      (screenWidth * startX).toInt, (screenHeight * startY).toInt,
      (screenWidth * endX).toInt, (screenHeight * endY).toInt, 1000
    )
    )
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
    //获取页面结构, 最多重试3次
    1 to 3 foreach (i => {
      asyncTask(20)(driver.source()) match {
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
          currentPageSource = RichData.toPrettyXML(xmlStr)
          currentPageDom=RichData.toDocument(currentPageSource)
          return currentPageSource
        }
        case None => {
          log.trace("get page source error")
        }
      }
    })
    currentPageSource
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
    retry{
      val s=driver.elementsByXPath(element.loc)
      0 until s.size() map(s.getIndex(_))
    } match {
      case Some(v) => {
        val arr = v.toList.distinct
        arr.length match {
          case len if len == 1 => {
            log.info("find by xpath success")
            currentElement=arr.head
            return true
          }
          case len if len > 1 => {
            log.warn(s"find count ${v.size}, you should check your dom file")
            //有些公司可能存在重名id
            arr.foreach(log.info)
            log.warn("just use the first one")
            currentElement=arr.head
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
    val xpath="(//*[@package!=''])[1]"
    getListFromXPath(xpath).head.getOrElse("package", "").toString
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

