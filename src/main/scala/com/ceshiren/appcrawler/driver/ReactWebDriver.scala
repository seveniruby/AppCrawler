package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.core.Crawler
import com.ceshiren.appcrawler.model.{PageSource, URIElement}
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.LogicUtils.asyncTask
import com.ceshiren.appcrawler.utils.TData
import org.openqa.selenium.Rectangle

import java.io.File
import scala.collection.mutable.ListBuffer

/**
  * Created by seveniruby on 2017/4/17.
  */

//todo: 用标准的class代替，用trait会让很多java工程师无法理解。
abstract class ReactWebDriver {

  var screenWidth = 0
  var screenHeight = 0
  var page: PageSource = null;
  val execResults: ListBuffer[String] = ListBuffer[String]()

  var loc = ""
  var index = 0
  var currentURIElement: URIElement = null

  var imagesDir = "images"
  var platformName = ""

  def findElements(element: URIElement, findBy: String = platformName): List[AnyRef]

  def findElement(element: URIElement, findBy: String = platformName): AnyRef = {
    //todo: 用其他定位方式优化
    log.info(s"find by uri element= ${element.elementUri()}")
    currentURIElement = element
    asyncTask(name = "findElementsByURI")(findElements(element, findBy)) match {
      case Left(v) => {
        val arr = v.distinct
        arr.length match {
          case len if len == 1 => {
            log.info("find by xpath success")
            return arr.head
          }
          case len if len > 1 => {
            log.warn(s"find count ${v.size}, you should check your dom file")
            //有些公司可能存在重名id
            arr.foreach(log.debug)
            log.warn("just use the first one")
            return arr.head
          }
          case len if len == 0 => {
            log.error("find by xpath error no element found")
          }
        }
      }
      case Right(e) => {
        log.error("find by xpath error")
      }
    }
    null
  }

  def getDeviceInfo(): Unit

  def screenshot(): File

  def back(): Unit = {}

  def backApp(): Unit = {}

  def launchApp(): Unit

  def getPageSource(): String


  //todo: 有的时候会出现极少内容的page source

  /**
   * <hierarchy class="hierarchy" height="1794" index="0" rotation="0" width="1080">
   * <android.widget.FrameLayout bounds="[0,0][1080,1794]" checkable="false" checked="false" class="android.widget.FrameLayout" clickable="false" displayed="true" enabled="true" focusable="false" focused="false" index="0" long-clickable="false" package="com.example.android.apis" password="false" scrollable="false" selected="false" text=""/>
   * </hierarchy>
   *
   */
  def getPageSourceWithRetry(): PageSource = {
    page = null
    log.info("start to get page source from appium")
    //获取页面结构, 最多重试3次
    var errorCount = 0
    var error: Throwable = null
    1 to 2 foreach (i => {
      asyncTask(40, name = "getPageSource")(getPageSource()) match {
        case Left(v) => {
          log.trace("get raw page source success")
          //          log.trace(v)
          //todo: wda返回的不是标准的xml
          log.debug(v)
          val xmlStr = v match {
            //todo: 更严格判断
            case blank if blank.getBytes.length < 500 => {
              log.error("may be page source is not enought")
              log.error(blank)
              null
            }
            case json if json.trim.charAt(0) == '{' => {
              log.info("json format maybe from wda")
              TData.fromJson[Map[String, String]](v).getOrElse("value", "")
            }
            case xml if xml.trim.charAt(0) == '<' => {
              log.info("xml format ")
              xml
            }
            case text: String => {
              log.error("page source format not support")
              log.error(text)
              text
            }
          }

          page = PageSource.getPagefromXML(xmlStr)
          if (page != null) {
            return page
          }
        }
        case Right(e) => {
          errorCount += 1
          log.error("get page source error")
          log.error(e.getMessage)
          error = e
        }
      }
      log.warn(s"retry ${i} times after 5s")
      Thread.sleep(5000)
    })

    page
  }

  def clickLocation(): Unit = {
    val point = currentURIElement.center()
  }

  def press(sec: Int): this.type = {
    this
  }

  def tap(): this.type

  def tapLocation(x: Int, y: Int): this.type

  def click(): this.type = {
    this
  }

  def reStartDriver(waitTime:Int=2000, action: String = "swipe"): Unit = {
  }

  def startDriver(): Unit = {
  }

  def stopDriver(): Unit = {
  }

  def longTap(): this.type = {
    this
  }

  def swipe(direction: String): Unit = {
    log.info(s"start swipe ${direction}")
    var startX = 0.0
    var startY = 0.0
    var endX = 0.0
    var endY = 0.0
    direction match {
      case "left" => {
        startX = 0.9
        startY = 0.5
        endX = 0.1
        endY = 0.5
      }
      case "right" => {
        startX = 0.1
        startY = 0.5
        endX = 0.9
        endY = 0.5
      }
      case "up" => {
        startX = 0.5
        startY = 0.9
        endX = 0.5
        endY = 0.1
      }
      case "down" => {
        startX = 0.5
        startY = 0.1
        endX = 0.5
        endY = 0.9
      }
      case _ => {
        startX = 0.9
        startY = 0.9
        endX = 0.1
        endY = 0.1
      }
    }
    swipe(startX, endX, startY, endY)
    sleep(1)
  }

  def swipe(startX: Double = 0.9, endX: Double = 0.1, startY: Double = 0.9, endY: Double = 0.1): Unit = {
  }


  def getUrl(): String = {
    ""
  }

  def getAppName(): String = {
    ""
  }

  def sendText(text: String): Unit = {

  }

  def existElement(): Boolean = {
    currentURIElement != null
  }

  def setWaitTimeOut(timeout: Long): Unit = {

  }

  //todo: 未完成
  def wait(key: String, timeout: Long = 5000): Unit = {
    getPageSourceWithRetry()
    val start = System.currentTimeMillis()
    var end: Long = 0
    var nodeList: List[Map[String, Object]] = List()
    do {
      log.trace(s"find ${key}")
      nodeList = page.getNodeListByKey(key)
      end = System.currentTimeMillis()
      Thread.sleep(500)
    } while (end - start < timeout && nodeList.isEmpty)
  }

  def event(keycode: String): Unit = {}

  def getRect(): Rectangle

  def sendKeys(content: String)

  def sleep(seconds: Double = 1.0F): Unit = {
    Thread.sleep((seconds * 1000).toInt)
  }


  //todo: xpath 2.0 support
  def getNodeListByKey(key: String): List[Map[String, Any]] = {
    page.getNodeListByKey(key)
  }

  //支持宽松查找，自动刷新查找，自动滚动查找
  def getNodeListByKeyWithRetry(key: String): List[Map[String, Any]] = {
    var array = getNodeListByKey(key)
    if (array.isEmpty) {
      getPageSourceWithRetry()
      log.debug("retry 1")
      array = getNodeListByKey(key)
    }

    if (array.isEmpty) {
      getPageSourceWithRetry()
      log.debug("retry 2")
      array = getNodeListByKey(key)
    }

    if (array.isEmpty) {
      getPageSourceWithRetry()
      log.debug("retry 3")
      array = getNodeListByKey(key)
    }
    return array

  }


  def attribute(key: String): String = {
    nodes().head(key).toString
  }

  def apply(key: String): String = {
    attribute(key)
  }

  def nodes(): List[Map[String, Any]] = {
    getNodeListByKey(loc)
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

    crawler.conf.maxDepth = maxDepth
    crawler.start(this)

  }

}