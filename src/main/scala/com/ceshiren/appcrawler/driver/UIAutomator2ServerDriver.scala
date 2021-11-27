package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.core.CrawlerConf
import com.ceshiren.appcrawler.model.URIElement
import com.ceshiren.appcrawler.utils.{DynamicEval, TData}
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.LogicUtils.retryToSuccess
import org.openqa.selenium.Rectangle

import java.io.File
import scala.collection.mutable
import scala.sys.process._

class UIAutomator2ServerDriver extends ReactWebDriver {
  private var adb = ""
  private val session = requests.Session()

  //本地映射的地址
  var systemPort = "6791"
  var serverPort = "6790"
  val driverPackageName = "io.appium.uiautomator2.server.test"
  val driverActivityName = "androidx.test.runner.AndroidJUnitRunner"
  var packageName = ""
  var activityName = ""
  var uuid = ""
  var otherApps: List[String] = List[String]()

  def this(configMap: Map[String, Any] = Map[String, Any]()) {
    this

//    val url = configMap.getOrElse("appium", "http://127.0.0.1:6790/wd/hub")
//    log.info(s"url=${url}")
    packageName = configMap.getOrElse("appPackage", "").toString
    activityName = configMap.getOrElse("appActivity", "").toString
    systemPort = configMap.getOrElse("systemPort", systemPort).toString
    uuid = configMap.getOrElse("uuid", "").toString
    adb = getAdb()
    //    log.info(configMap.toString())
    if (systemPort.isEmpty) {
      log.info(s"No systemPort Set In Config,Use Default Port: ${systemPort}")
    }
    otherApps = configMap.getOrElse("otherApps", List[String]()).asInstanceOf[List[String]]
    // 安装辅助APP
    installOtherApps()
    // 确认设备中Driver状态
    val apkPath = adb(s"shell 'pm list packages | grep ${driverPackageName} ||:'")
    if (apkPath.indexOf(s"${driverPackageName}") == -1) {
      log.info("Not Exist In Device,Need Install")
    }

    //设备driver连接设置
    initDriver()

    if (configMap.getOrElse("noReset", "").toString.toLowerCase.equals("false")
      && !packageName.contains("tencent")) {
      adb(s"shell pm clear ${packageName}")
    } else {
      log.info("need need to reset app")
    }

    if (packageName.nonEmpty && configMap.getOrElse("dontStopAppOnReset", "").toString.toLowerCase.equals("true")) {
      adb(s"shell am start -W -n ${packageName}/${activityName}")
    }
    else if (packageName.nonEmpty && configMap.getOrElse("dontStopAppOnReset", "").toString.toLowerCase.equals("false")) {
      adb(s"shell am start -S -W -n ${packageName}/${activityName}")
    } else {
      //小程序不能运行am start
      log.info("dont run am start")
    }
  }

  // 安装辅助APP
  def installOtherApps(): Unit = {
    log.info("Install otherApps")
    otherApps.foreach(app => {
      log.info(s"Install App To Device From ${app}")
      adb(s"install '${app}'")
    })
  }


  def setPackage(): Unit = {
    val r = session.post(s"${getServerUrl}/package?package=${packageName}")
    log.info(r)
  }

  //设备driver连接设置
  def initDriver(): Unit = {
    // 启动Driver
    driverStart()
    //设置端口转发，将driver的端口映射到本地，方便进行请求
    adb(s"forward tcp:${systemPort} tcp:${serverPort}")
  }

  def driverStart(): Unit = {
    adb(s"shell 'am force-stop ${driverPackageName} && ps | grep ${driverPackageName} ||: ' ")
    adb(s"shell 'am force-stop ${driverPackageName} && ps | grep ${driverPackageName} ||: ' ")
    //放入后台
    val cmd=s"bash -c '${adb} shell am instrument -w -e disableAnalytics true ${driverPackageName}/${driverActivityName} & ' "
    Runtime.getRuntime.exec(cmd)
    retryToSuccess(timeoutMS = 20000, name = "wait driver")(session.get(s"${getServerUrl}/sessions").text().contains("sessionId"))
  }

  //拼接Driver访问地址
  def getServerUrl: String = {
    s"http://127.0.0.1:${systemPort}/wd/hub"
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
    val file = File.createTempFile("tmp", ".png")
    log.info(file.getAbsolutePath)
    val cmd = s"${adb} exec-out screencap -p"
    log.info(cmd)
    (cmd #> file).!!
    file
  }

  //todo: 重构到独立的trait中


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
    val res = session.post(s"${getServerUrl}/session", data = "{\"capabilities\": {}}").text()
    val sessionId = res.split("\"")(3)
    log.debug(s"sessionid = ${sessionId}")
    val r=session.get(s"${getServerUrl}/session/${sessionId}/source").text()
    session.delete(s"${getServerUrl}/session/${sessionId}")
    r
  }

  override def getAppName(): String = {
    page.getNodeListByKey("/*/*").head.getOrElse("package", "").toString
  }

  override def getUrl(): String = {
    ""
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

  override def reStartDriver(): Unit = {
    log.info("reStartDriver")
    driverStart()
    setPackage()
    // todo:需要优化
    // 重启服务后需要通过页面动作触发page source刷新，保证能够获取到最新的界面数据
    swipe(0.5, 0.4, 0.5, 0.5)
    Thread.sleep(1000)
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

  override def adb(command: String): String = {
    shell(s"${adb} ${command}")
  }

  def shell(cmd: String): String = {
    log.info(cmd)
    val result = cmd.!!
    log.info(result)
    result
  }

}

