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

class UIAutomator2ServerDriver extends AdbDriver {
  private val session = requests.Session()

  //本地映射的地址
  var systemPort = "6791"
  var serverPort = "6790"
  val driverPackageName = "io.appium.uiautomator2.server.test"
  val driverActivityName = "androidx.test.runner.AndroidJUnitRunner"
  var otherApps: List[String] = List[String]()
  var sessionid = ""

  var daemon: Thread = _

  def this(configMap: Map[String, Any] = Map[String, Any]()) {
    this

    //    val url = configMap.getOrElse("appium", "http://127.0.0.1:6790/wd/hub")
    //    log.info(s"url=${url}")
    packageName = configMap.getOrElse("appPackage", "").toString
    activityName = configMap.getOrElse("appActivity", "").toString
    systemPort = configMap.getOrElse("systemPort", systemPort).toString
    uuid = configMap.getOrElse("uuid", "").toString
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

    daemon = new Thread(() => {
      log.info("daemon start")
      adb(s"shell am instrument -w -e disableAnalytics true ${driverPackageName}/${driverActivityName} ")
      log.info("daemon finish")
    })
    daemon.start()
    retryToSuccess(timeoutMS = 20000, name = "wait driver")(session.get(s"${getServerUrl}/sessions").text().contains("sessionId"))
    retryToSuccess(timeoutMS = 20000, name = "wait session created") {
      val res = session.post(s"${getServerUrl}/session", data = "{\"capabilities\": {}}").text()
      sessionid = res.split("\"")(3)
      log.debug(s"sessionid = ${sessionid}")
      sessionid.nonEmpty
    }
    log.debug(session.get(s"${getServerUrl}/sessions").text())
  }

  override def stopDriver(): Unit = {
    session.delete(s"${getServerUrl}/session/${sessionid}")
    log.info(s"stop session ${sessionid}")
    daemon.stop()
  }

  //拼接Driver访问地址
  def getServerUrl: String = {
    s"http://127.0.0.1:${systemPort}/wd/hub"
  }


  override def getPageSource(): String = {
    val r = session.get(s"${getServerUrl}/session/${sessionid}/source").text()
    r
  }


  override def getAppName(): String = {
    page.getNodeListByKey("/*/*").head.getOrElse("package", "").toString
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


}

