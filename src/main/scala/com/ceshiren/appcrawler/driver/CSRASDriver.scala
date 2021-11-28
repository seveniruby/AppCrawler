package com.ceshiren.appcrawler.driver

import com.ceshiren.appcrawler.GetAPKPackage.getPackageName
import com.ceshiren.appcrawler.utils.Log.log
import com.ceshiren.appcrawler.utils.LogicUtils.retryToSuccess

/**
  * Created by seveniruby on 18/10/31.
  */
class CSRASDriver extends AdbDriver {
  private val session = requests.Session()

  //csras本地映射的地址
  systemPort = "7778"
  otherApps = List[String]()

  def this(configMap: Map[String, Any] = Map[String, Any]()) {
    this
    initConfig(configMap)
    // 安装辅助APP
    installOtherApps()
    // 确认设备中Driver状态
    val apkPath = adb(s"shell 'pm list packages | grep com.hogwarts.csruiautomatorserver ||:'")
    if (apkPath.indexOf("com.hogwarts.csruiautomatorserver") == -1) {
      log.info("CSRASDriver Not Exist In Device,Need Install")
      otherApps = configMap.getOrElse("otherApps", List[String]()).asInstanceOf[List[String]]
      // 安装辅助APP
      installOtherApps()
      // 确认设备中Driver状态
      if (!getAPKInstallStatus("com.hogwarts.csruiautomatorserver")) {
        log.info("CSRUIAutomatorServer Not Exist In Device,Need Install")
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
        log.info("dont run am start")
      }
    }
  }

  def getAPKInstallStatus(packageName: String): Boolean = {
    // 确认设备中Driver状态
    val apkPath = adb(s"shell 'pm list packages | grep package:${packageName}" + "$ ||:'")
    apkPath.indexOf(packageName) != -1
  }

  // 安装辅助APP
  def installOtherApps(): Unit = {
    log.info("Install otherApps")
    otherApps.foreach(app => {
      val APKPackageName = getPackageName(app)
      if (APKPackageName == null) {
        log.info(s"Can Not Get PackageName From $app")
        log.info(s"Install App $app To Device")
        adb(s"install '$app'")
      } else {
        if (getAPKInstallStatus(APKPackageName)) {
          log.info(s"$app Already Exist In Device,Skip Install")
        } else {
          log.info(s"Install App $app To Device")
          adb(s"install '$app'")
        }
      }
    })
  }


  def setPackage(): Unit = {
    try {
      val r = session.post(s"${getServerUrl}/package?package=${packageName}")
      log.info(r)
    } catch {
      case e: Exception => log.error(e.printStackTrace())
    }
  }

  //设备driver连接设置
  def initDriver(): Unit = {
    // 给Driver设置权限，使驱动可以自行开启辅助功能
    //    adb(s"shell pm grant com.hogwarts.csruiautomatorserver android.permission.WRITE_SECURE_SETTINGS")


    //设置端口转发，将driver的端口映射到本地，方便进行请求
    adb(s"forward tcp:${systemPort} tcp:7777")
    // 启动Driver
    driverStart()

    // 等待远程服务连接完毕
    // todo:将等待改为通过轮询接口判断设备上的服务是否启动
    setPackage()
    //获取包过滤参数
    //    val packageFilter =
    log.info(s"Driver Filter Package is ${getPackageFilter}")

  }

  def driverStart(): Unit = {
    adb(s"shell 'am force-stop com.hogwarts.csruiautomatorserver && ps | grep com.hogwarts.csruiautomatorserver ||: ' ")
    adb(s"shell 'am force-stop com.hogwarts.csruiautomatorserver && ps | grep com.hogwarts.csruiautomatorserver ||: ' ")
    adb(s"shell settings put secure enabled_accessibility_services com.hogwarts.csruiautomatorserver/.CSRAccessibilityService")
    Thread.sleep(500)
    // Android11需要多关闭重启一次服务才能够正常的启动起来。
    adb(s"shell settings put secure enabled_accessibility_services com.hogwarts.csruiautomatorserver")
    adb(s"shell settings put secure enabled_accessibility_services com.hogwarts.csruiautomatorserver/.CSRAccessibilityService")
    retryToSuccess(timeoutMS = 20000, name = "wait csras driver")(session.get(s"${getServerUrl}/ping").text() == "pong")
  }

  //拼接Driver访问地址
  def getServerUrl: String = {
    val driverUrl = "http://127.0.0.1"
    driverUrl + ":" + systemPort
  }

  def getPackageFilter: String = {
    //通过发送请求，设置关注的包名，过滤掉多余的数据
    session.get(s"${getServerUrl}/package").text()
  }

  override def backApp(): Unit = {
    adb(s"shell am start -W -n ${packageName}/${activityName}")
  }

  override def getPageSource(): String = {
    session.get(s"${getServerUrl}/source").text()
  }

  override def getAppName(): String = {
    val appName = session.get(s"${getServerUrl}/fullName").text().split('/').head
    log.info(s"PackageName is ${appName}")
    appName
  }

  override def getUrl(): String = {
    val appUrl = session.get(s"${getServerUrl}/fullName").text().split('/').last.stripLineEnd
    log.info(s"ActivityName is ${appUrl}")
    appUrl
  }


  // 重启Driver服务
  // waitTime: 重启中途步骤的等待时间，会在启动服务之后/结束重启之前进行两次等待，单位为 毫秒
  // action: 重启后用于重新获取页面page_source的动作，默认swipe，在页面中间小幅度滑动。支持statusbar，将系统通知栏下拉和还原
  override def reStartDriver(waitTime: Int = 2000, action: String = "swipe"): Unit = {
    log.info("reStartDriver")
    driverStart()
    Thread.sleep(waitTime)
    log.info(s"Wait ${waitTime}ms")
    setPackage()
    action match {
      case "swipe" =>
        // 滑动屏幕，刷新page_source
        swipe(0.5, 0.4, 0.5, 0.5)
      case "statusbar" =>
        // 重启服务后需要通过呼出和收回系统通知栏触发page source刷新，保证能够获取到最新的界面数据
        adb("shell cmd statusbar expand-notifications")
        Thread.sleep(2000)
        adb("shell cmd statusbar collapse")
    }
    Thread.sleep(waitTime)
    log.info(s"Wait ${waitTime}ms")
  }
}

