package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.core.CrawlerConf
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.Log.log
import org.openqa.selenium.Rectangle

import java.io.File
import scala.sys.process._

/**
  * Created by seveniruby on 18/10/31.
  */
class AdbDriver extends ReactWebDriver {
  var conf: CrawlerConf = _
  val adb: String = getAdb()
  var uuid = ""

  var systemPort = "7777"
  var otherApps: List[String] = List[String]()

  var packageName = ""
  var activityName = ""

  var currentApp = ""
  var currentUrl = ""


  def this(configMap: Map[String, Any] = Map[String, Any]()) {
    this
    initConfig(configMap)
    if (configMap.getOrElse("noReset", "").toString.equals("false")) {
      shell(s"${adb} shell pm clear ${packageName}")
    } else {
      log.info("need need to reset app")
    }
    shell(s"${adb} shell am start -W -n ${packageName}/${activityName}")
  }

  def initConfig(configMap: Map[String, Any]) = {
    packageName = configMap.getOrElse("appPackage", "").toString
    activityName = configMap.getOrElse("appActivity", "").toString
    systemPort = configMap.getOrElse("systemPort", systemPort).toString
    uuid = configMap.getOrElse("uuid", "").toString
    //    log.info(configMap.toString())
    if (systemPort.isEmpty) {
      log.info(s"No systemPort Set In Config,Use Default Port: ${systemPort}")
    }
    otherApps = configMap.getOrElse("otherApps", List[String]()).asInstanceOf[List[String]]
  }


  override def event(keycode: String): Unit = {
    adb(s"shell input keyevent ${keycode}")
    log.info(s"event=${keycode}")
  }

  //todo: outside of Raster 问题
  override def getDeviceInfo(): Unit = {
    val size = adb(s"shell wm size").split(' ').last.split('x')
    screenHeight = size.last.trim.toInt
    screenWidth = size.head.trim.toInt
    log.info(s"screenWidth=${screenWidth} screenHeight=${screenHeight}")
  }

  override def swipe(startX: Double = 0.9, startY: Double = 0.1, endX: Double = 0.9, endY: Double = 0.1): Unit = {
    val xStart = startX * screenWidth
    val xEnd = endX * screenWidth
    val yStart = startY * screenHeight
    val yEnd = endY * screenHeight
    log.info(s"swipe screen from (${xStart},${yStart}) to (${xEnd},${yEnd})")
    adb(s"shell input swipe ${xStart} ${yStart} ${xEnd} ${yEnd}")
  }


  override def screenshot(): File = {
    //    val file = File.createTempFile("tmp", ".png")
    //    log.info(s"screenshot to ${file.getCanonicalPath}")
    //    adb(s"exec-out screencap -p > ${file.getCanonicalPath}", verbose = false)
    //    file

    val file = File.createTempFile("tmp", ".png")
    log.info(file.getAbsolutePath)
    val cmd = s"${adb} exec-out screencap -p"
    log.info(cmd)
    (cmd #> file).!!
    file
  }

  override def click(): this.type = {
    val center = currentURIElement.center()
    adb(s"shell input tap ${center.x} ${center.y}")
    this
  }

  override def tap(): this.type = {
    click()
  }

  override def tapLocation(x: Int, y: Int): this.type = {
    val pointX = x * screenWidth
    val pointY = y * screenHeight
    adb(s"shell input tap ${pointX} ${pointY}")
    this
  }

  override def longTap(): this.type = {
    val center = currentURIElement.center()
    log.info(s"longTap element in (${center.x},${center.y})")
    adb(s"shell input swipe ${center.x} ${center.y} ${center.x + 0.1} ${center.y + 0.1} 2000")
    this
  }

  override def back(): Unit = {
    adb(s"shell input keyevent 4")
  }


  override def backApp(): Unit = {
    adb(s"shell am start -W -n ${packageName}/${activityName}")
  }

  override def getPageSource(): String = {
    //todo: null root问题, idle wait timeout问题, Uiautomator dump太鸡肋了，没啥用, https://github.com/appium/appium-uiautomator2-server/pull/80
    adb(
      s"""shell eval "[ -f /data/local/tmp/source.xml ] && rm /data/local/tmp/source.xml >/dev/null 2>&1 ;
         |uiautomator dump /data/local/tmp/source.xml > /dev/null;
         |cat /data/local/tmp/source.xml || echo ERROR " """.stripMargin)
  }

  override def getAppName(): String = {
    //    val appName=shell(s"${adb} shell dumpsys window windows | grep mFocusedApp=").split('/').head.split(' ').last
    val res = adb("shell \"dumpsys window windows | grep 'Focus.* .*/.*}' | grep -o '[^ /]*/[^ }]*' | head -1 \" ").split("/")
    currentApp = res.head
    currentUrl = res.last
    currentApp
  }

  override def getUrl(): String = {
    currentUrl
  }

  override def getRect(): Rectangle = {
    //selenium下还没有正确的赋值，只能通过api获取
    if (currentURIElement.getHeight != 0) {
      //log.info(s"location=${location} size=${size} x=${currentURIElement.x} y=${currentURIElement.y} width=${currentURIElement.width} height=${currentURIElement.height}" )
      new Rectangle(currentURIElement.getX, currentURIElement.getY, currentURIElement.getHeight, currentURIElement.getWidth)
    } else {
      log.error("rect height < 0")
      return null
    }
  }

  override def sendKeys(content: String): Unit = {
    tap()
    adb(s"shell input text ${content}")
  }

  override def launchApp(): Unit = {
    //driver.get(capabilities.getCapability("app").toString)
    back()
  }

  override def findElements(element: URIElement, findBy: String): List[AnyRef] = {
    List(element)
  }

  def shell(cmd: String, verbose: Boolean = true): String = {
    if (verbose) {
      log.info(cmd)
    }
    val result = cmd.!!
    if (verbose) {
      log.info(result)
    }
    result
  }

  def getAdb(): String = {
    var adbCMD = ""
    if (System.getenv("ANDROID_HOME") != null) {
      adbCMD = List(System.getenv("ANDROID_HOME"), "platform-tools/adb").mkString(File.separator)
    } else {
      adbCMD = "adb"
    }
    if (uuid != null && uuid.nonEmpty) {
      s"${adbCMD} -s ${uuid}"
    } else {
      adbCMD
    }
  }

  override def sendText(text: String): Unit = {
    adb(s"shell am broadcast -a ADB_INPUT_TEXT --es msg '${text}'")
  }

  def adb(command: String, verbose: Boolean = true): String = {
    shell(s"${adb} ${command}", verbose)
  }


}

