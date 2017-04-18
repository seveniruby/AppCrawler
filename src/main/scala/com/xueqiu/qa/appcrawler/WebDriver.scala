package com.xueqiu.qa.appcrawler

import java.io.File
import java.util.concurrent.{Callable, Executors, TimeUnit, TimeoutException}

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.{Rectangle, WebElement}
import org.openqa.selenium.remote.DesiredCapabilities
import org.w3c.dom.Document

import scala.util.{Failure, Success, Try}

/**
  * Created by seveniruby on 2017/4/17.
  */
trait WebDriver extends CommonLog {

  val capabilities = new DesiredCapabilities()

  var screenWidth = 0
  var screenHeight = 0
  var currentPageDom: Document = null


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
        log.info(s"async task success")
        Some(v)
      }
      case Failure(e) => {
        e match {
          case e: TimeoutException => {
            log.error(s"${timeout} seconds timeout")
          }
          case _ => {
            log.error("exception")
            log.error(e.getMessage)
            log.error(e.getStackTrace.mkString("\n"))
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




}