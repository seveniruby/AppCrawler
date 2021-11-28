package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.LogicUtils.retryToSuccess

class UIAutomator2ServerDriver extends AdbDriver {
  private val session = requests.Session()

  //本地映射的地址
  systemPort = "6791"
  var serverPort = "6790"
  val driverPackageName = "io.appium.uiautomator2.server.test"
  val driverActivityName = "androidx.test.runner.AndroidJUnitRunner"
  otherApps = List[String]()
  var sessionid = ""

  var daemon: Thread = _

  def this(configMap: Map[String, Any] = Map[String, Any]()) {
    this
    initConfig(configMap)
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


  //设备driver连接设置
  def initDriver(): Unit = {
    //设置端口转发，将driver的端口映射到本地，方便进行请求
    adb(s"forward tcp:${systemPort} tcp:${serverPort}")
    // 启动Driver
    driverStart()
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

  override def reStartDriver(waitTime: Int, action: String): Unit = {
    log.info("reStartDriver")
    driverStart()
    // todo:需要优化
    // 重启服务后需要通过页面动作触发page source刷新，保证能够获取到最新的界面数据
    swipe(0.5, 0.4, 0.5, 0.5)
    Thread.sleep(1000)

  }

}

