package com.testerhome.appcrawler.driver

import java.io.File
import java.util.concurrent.{Callable, Executors, TimeUnit, TimeoutException}

import com.testerhome.appcrawler._
import com.testerhome.appcrawler.data.AbstractElement
import org.openqa.selenium.Rectangle
import org.openqa.selenium.remote.DesiredCapabilities
import org.w3c.dom.Document

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 2017/4/17.
  */

//todo: 用标准的class代替，用trait会让很多java工程师无法理解。
abstract class ReactWebDriver extends CommonLog {

  var screenWidth = 0
  var screenHeight = 0
  var currentPageDom: Document = null
  var currentPageSource: String=""
  val appiumExecResults=ListBuffer[String]()

  var loc = ""
  var index = 0
  var currentURIElement: AbstractElement= AppCrawler.factory.generateElement

  var imagesDir="images"
  var platformName=""



  def findElementsByURI(element: AbstractElement, findBy:String=platformName): List[AnyRef]
  def findElementByURI(element: AbstractElement, findBy:String=platformName): AnyRef= {
    //todo: 用其他定位方式优化
    log.info(s"find by uri element= ${element.elementUri()}")
    currentURIElement=element
    asyncTask(name = "findElementsByURI")(findElementsByURI(element, findBy)) match {
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
            arr.foreach(log.info)
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
  def launchApp()
  def getPageSource(): String

  def getPageSourceWithRetry(): String = {
    currentPageSource=null
    currentPageDom=null
    log.info("start to get page source from appium")
    //获取页面结构, 最多重试3次
    var errorCount=0
    var error: Throwable=null
    1 to 2 foreach (i => {
      asyncTask(40, name = "getPageSource")(getPageSource) match {
        case Left(v) => {
          log.trace("get page source success")
          //todo: wda返回的不是标准的xml
          val xmlStr=v match {
            case json if json.trim.charAt(0)=='{' => {
              log.info("json format maybe from wda")
              TData.fromJson[Map[String, String]](v).getOrElse("value", "")
            }
            case xml if xml.trim.charAt(0)=='<' =>{
              log.info("xml format ")
              xml
            }
            case text:String => {
              log.error("page source format not support")
              log.error(text)
              text
            }
          }
          Try(XPathUtil.toDocument(xmlStr)) match {
            case Success(v) => {
              currentPageDom = v
              log.debug(v)
              currentPageSource = XPathUtil.toPrettyXML(xmlStr)
              log.debug(currentPageSource)
              return currentPageSource
            }
            case Failure(e) => {
              log.warn("convert to xml fail")
              log.warn(xmlStr)
              currentPageDom = null
              currentPageSource = null
            }
          }

        }
        case Right(e) => {
          errorCount+=1
          log.error("get page source error")
          error=e
        }
      }
      log.warn(s"retry ${i} times")
      Thread.sleep(5000)
    })
    currentPageSource
  }

  def clickLocation(): Unit = {
    val point=currentURIElement.center()
  }
  def press(sec: Int): this.type = { this }
  def tap(): this.type
  def click(): this.type = { this }
  def longTap(): this.type = { this }
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

  def getAppName(): String ={
    ""
  }

  def asyncTask[T](timeout: Int = 30, name:String ="", needThrow:Boolean=false)(callback: => T): Either[T, Throwable] = {
    //todo: 异步线程消耗资源厉害，需要改进
    val start=System.currentTimeMillis()
    Try({
      val task = Executors.newSingleThreadExecutor().submit(new Callable[T]() {
        def call(): T = {
          callback
        }
      })
      if(timeout<0){
        task.get()
      }else {
        task.get(timeout, TimeUnit.SECONDS)
      }

    }) match {
      case Success(v) => {
        val end=System.currentTimeMillis()
        appiumExecResults.append("success")
        val use=(end-start)/1000d
        if(use>=1){
          log.info(s"use time $use seconds name=${name} result=success")
        }
        Left(v)
      }
      case Failure(e) => {
        val end=System.currentTimeMillis()
        val use=(end-start)/1000d
        if(use>=1){
          log.info(s"use time $use seconds name=${name} result=error")
        }
        if(needThrow){
          throw e
        }
        e match {
          case e: TimeoutException => {
            log.error(s"${timeout} seconds timeout")
          }
          case _ => {
            handleException(e)
          }
        }
        Right(e)
      }
    }
  }

  def handleException(e: Throwable): Unit ={
    var exception = e
    do {
      log.error(exception.getLocalizedMessage)
      exception.getStackTrace.foreach(log.error)
      if(exception.getCause!=null){
        log.error("find more cause")
      }else{
        log.error("exception finish")
      }
      exception=exception.getCause
    }while(exception!=null)
  }

  def tryAndCatch[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        log.info("retry execute success")
        Some(v)
      }
      case Failure(e) => {
        handleException(e)
        None
      }
    }
  }

  def event(keycode: Int): Unit = {}
  def mark(fileName: String, newImageName:String,  x: Int, y: Int, w: Int, h: Int): Unit
  def getRect(): Rectangle

  def sendKeys(content:String)

  def sleep(seconds: Double = 1.0F): Unit = {
    Thread.sleep((seconds * 1000).toInt)
  }



  //todo: xpath 2.0 support
  def getNodeListByKey(key:String): List[Map[String, Any]] ={
    XPathUtil.getNodeListByKey(key, currentPageDom)
  }

  //支持宽松查找，自动刷新查找，自动滚动查找
  def findMapWithRetry(key:String): List[Map[String, Any]] ={
    var array=getNodeListByKey(key)
    if(array.size==0){
      getPageSourceWithRetry()
      log.debug("retry 1")
      array=getNodeListByKey(key)
    }

    if(array.size==0){
      getPageSourceWithRetry()
      log.debug("retry 2")
      array=getNodeListByKey(key)
    }

    if(array.size==0){
      getPageSourceWithRetry()
      log.debug("retry 3")
      array=getNodeListByKey(key)
    }
    return array

  }


  def attribute(key: String): String = {
    nodes().head.get(key).get.toString
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