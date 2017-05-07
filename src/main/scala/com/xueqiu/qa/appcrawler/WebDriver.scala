package com.xueqiu.qa.appcrawler

import java.io.File
import java.util.concurrent.{Callable, Executors, TimeUnit, TimeoutException}

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.{Rectangle, WebElement}
import org.openqa.selenium.remote.DesiredCapabilities
import org.w3c.dom.Document

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 2017/4/17.
  */
trait WebDriver extends CommonLog {

  val capabilities = new DesiredCapabilities()

  var screenWidth = 0
  var screenHeight = 0
  var currentPageDom: Document = null
  var currentPageSource: String=""
  val appiumExecResults=ListBuffer[String]()


  def config(key: String, value: Any): Unit = {
    capabilities.setCapability(key, value)
  }

  def stop(): Unit = {
  }

  def hideKeyboard(): Unit = {

  }

  def findElementByUrlElement(element: URIElement): Boolean= {
    false
  }

  def getDeviceInfo(): Unit = {
  }

  def screenshot(): File = { null }

  def back(): Unit = {}

  def backApp(): Unit = {}

  def getPageSource(): String = { "" }

  def tap(): this.type = { this }
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

  def swipe(startX: Double = 0.9, endX: Double = 0.1, startY: Double = 0.9, endY: Double = 0.1): Option[_] = {
    None
  }




  def dsl(command: String): Unit = {
    log.info(s"eval ${command}")
    Try(Runtimes.eval(command)) match {
      case Success(v) => log.info(v)
      case Failure(e) => log.warn(e.getMessage)
    }
    log.info("eval finish")
    //new Eval().inPlace(s"com.xueqiu.qa.appcrawler.MiniAppium.${command.trim}")
  }


  def getUrl(): String = {
    ""
  }

  def getAppName(): String ={
    ""
  }

  def asyncTask[T](timeout: Int = 30, restart: Boolean = false)(callback: => T): Option[T] = {
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

    })  match {
      case Success(v) => {
        appiumExecResults.append("success")
        Some(v)
      }
      case Failure(e) => {
        e match {
          case e: TimeoutException => {
            log.error(s"${timeout} seconds timeout")
            appiumExecResults.append("timeout")
          }
          case _ => {
            log.error("exception")
            log.error(e.getMessage)
            log.error(e.getStackTrace.mkString("\n"))
            appiumExecResults.append(e.getMessage)
          }
        }
        None
      }
    }
  }

  def retry[T](r: => T): Option[T] = {
    Try(r) match {
      case Success(v) => {
        log.info("retry execute success")
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


  def event(keycode: Int): Unit = {}
  def mark(fileName: String, newImageName:String,  x: Int, y: Int, w: Int, h: Int): Unit = {}
  def getRect(): Rectangle ={
    new Rectangle(0, 0, 0, 0)
  }

  def sendKeys(content:String): Unit ={

  }

  def sleep(seconds: Double = 1.0F): Unit = {
    Thread.sleep((seconds * 1000).toInt)
  }

  //todo: xpath 2.0 support
  def getListFromXPath(key:String): List[Map[String, Any]] ={
    key match {
      //xpath
      case xpath if Array('/', '(').contains(xpath.head) => {
        RichData.getListFromXPath(xpath, currentPageDom)
      }
      case regex if key.contains(".*") || key.startsWith("^") => {
        RichData.getListFromXPath("//*", currentPageDom).filter(m=>{
          m("name").toString.matches(regex) ||
            m("label").toString.matches(regex) ||
            m("value").toString.matches(regex)
        })
      }
      case str: String => {
        RichData.getListFromXPath("//*", currentPageDom).filter(m=>{
          m("name").toString.contains(str) ||
            m("label").toString.contains(str) ||
            m("value").toString.contains(str)
        })
      }
    }
  }

}